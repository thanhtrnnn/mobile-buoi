#!/bin/bash
set -e

# Dùng: ./build_and_run.sh [bai03|bai04]   (mặc định bai04)
# Mỗi bài là một app riêng: package Kotlin riêng, thư mục res riêng, manifest riêng.
BAI="${1:-bai04}"
case "$BAI" in
  bai03|bai04) ;;
  *) echo "Chỉ nhận bai03 hoặc bai04, không phải '$BAI'"; exit 1 ;;
esac

SDK="$HOME/Library/Android/sdk"
BT="$SDK/build-tools/36.0.0"
PLATFORM="$SDK/platforms/android-37.0/android.jar"
STUDIO="/Applications/Android Studio.app/Contents"
JAVA_HOME="$STUDIO/jbr/Contents/Home"
KOTLINC="$STUDIO/plugins/Kotlin/kotlinc"

PKG="ptit.cnpm1.tranxuanthanh.$BAI"
SRC="app/src/main/java/ptit/cnpm1/tranxuanthanh/$BAI"
RES="app/src/main/res-$BAI"
MANIFEST="app/src/main/AndroidManifest-$BAI.xml"
OUT="build/$BAI"

# Thư viện ngoài (đã giải nén sẵn từ file .aar trong thư mục vendor/), dùng chung cho mọi bài
CL_JAR="vendor/constraintlayout/classes.jar"
CL_CORE_JAR="vendor/constraintlayout-core.jar"
LIBS="$CL_JAR:$CL_CORE_JAR"

echo "=== Đang build $BAI ($PKG) ==="

echo "=== 1. Chuẩn bị thư mục build ==="
rm -rf "$OUT"
mkdir -p "$OUT/gen" "$OUT/classes" "$OUT/apk"

echo "=== 2. Biên dịch giao diện XML (aapt2 compile) ==="
$BT/aapt2 compile --dir "$RES" -o "$OUT/compiled_res.zip"
$BT/aapt2 compile --dir vendor/constraintlayout/res -o "$OUT/lib_res.zip"

echo "=== 3. Liên kết tài nguyên và sinh file R.java (aapt2 link) ==="
$BT/aapt2 link -I "$PLATFORM" \
  --manifest "$MANIFEST" \
  --min-sdk-version 24 \
  --target-sdk-version 35 \
  --java "$OUT/gen" \
  --extra-packages androidx.constraintlayout.widget \
  -o "$OUT/app-unaligned.apk" \
  "$OUT/compiled_res.zip" \
  "$OUT/lib_res.zip" \
  --auto-add-overlay

echo "=== 4a. Biên dịch R.java sang bytecode (javac) ==="
# Cần biên dịch thật R.java vì thư viện dựng sẵn tham chiếu R$styleable lúc chạy
JAVA_HOME="$JAVA_HOME" "$JAVA_HOME/bin/javac" \
  -source 17 -target 17 \
  -cp "$PLATFORM" \
  -d "$OUT/classes" \
  $(find "$OUT/gen" -name "*.java")

echo "=== 4b. Biên dịch mã Kotlin sang bytecode .class (kotlinc) ==="
JAVA_HOME="$JAVA_HOME" \
"$KOTLINC/bin/kotlinc" \
  -cp "$PLATFORM:$KOTLINC/lib/kotlin-stdlib.jar:$LIBS:$OUT/classes" \
  -d "$OUT/classes" \
  $(find "$SRC" -name "*.kt")

echo "=== 5. Chuyển bytecode sang mã máy Android DEX (d8) ==="
JAVA_HOME="$JAVA_HOME" \
$BT/d8 --lib "$PLATFORM" --min-api 24 --output "$OUT/apk/" \
  $(find "$OUT/classes" -name "*.class") \
  "$KOTLINC/lib/kotlin-stdlib.jar" \
  "$CL_JAR" \
  "$CL_CORE_JAR"

echo "=== 6. Đóng gói DEX vào file APK ==="
(cd "$OUT/apk" && zip -u ../app-unaligned.apk *.dex)

echo "=== 7. Tối ưu hóa file APK (zipalign) ==="
$BT/zipalign -f -p 4 "$OUT/app-unaligned.apk" "$OUT/app-aligned.apk"

echo "=== 8. Ký số APK (apksigner) ==="
JAVA_HOME="$JAVA_HOME" \
$BT/apksigner sign --ks ~/.android/debug.keystore --ks-pass pass:android \
  --ks-key-alias androiddebugkey --key-pass pass:android \
  --out "$OUT/app-signed.apk" "$OUT/app-aligned.apk"

echo "=== 9. Cài đặt lên máy ảo (adb install) ==="
$SDK/platform-tools/adb install -r "$OUT/app-signed.apk"

echo "=== 10. Khởi chạy ứng dụng ==="
$SDK/platform-tools/adb shell am force-stop $PKG
$SDK/platform-tools/adb shell am start -n $PKG/$PKG.LoginAct

echo "=== Hoàn tất! $BAI đã chạy trên máy ảo. ==="
