#!/bin/bash
set -e

SDK="$HOME/Library/Android/sdk"
BT="$SDK/build-tools/36.0.0"
PLATFORM="$SDK/platforms/android-37.0/android.jar"
STUDIO="/Applications/Android Studio.app/Contents"
JAVA_HOME="$STUDIO/jbr/Contents/Home"
KOTLINC="$STUDIO/plugins/Kotlin/kotlinc"
PKG="ptit.cnpm1.tranxuanthanh.bai03"

# Thư viện ngoài (đã giải nén sẵn từ file .aar trong thư mục vendor/)
CL_JAR="vendor/constraintlayout/classes.jar"
CL_CORE_JAR="vendor/constraintlayout-core.jar"
LIBS="$CL_JAR:$CL_CORE_JAR"

echo "=== 1. Chuẩn bị thư mục build ==="
rm -rf build
mkdir -p build/compiled_res build/gen build/classes build/apk

echo "=== 2. Biên dịch giao diện XML (aapt2 compile) ==="
$BT/aapt2 compile --dir app/src/main/res -o build/compiled_res.zip
$BT/aapt2 compile --dir vendor/constraintlayout/res -o build/lib_res.zip

echo "=== 3. Liên kết tài nguyên và sinh file R.java (aapt2 link) ==="
$BT/aapt2 link -I "$PLATFORM" \
  --manifest app/src/main/AndroidManifest.xml \
  --min-sdk-version 24 \
  --target-sdk-version 35 \
  --java build/gen \
  --extra-packages androidx.constraintlayout.widget \
  -o build/app-unaligned.apk \
  build/compiled_res.zip \
  build/lib_res.zip \
  --auto-add-overlay

echo "=== 4a. Biên dịch R.java sang bytecode (javac) ==="
# Cần biên dịch thật R.java vì thư viện dựng sẵn tham chiếu R$styleable lúc chạy
JAVA_HOME="$JAVA_HOME" "$JAVA_HOME/bin/javac" \
  -source 17 -target 17 \
  -cp "$PLATFORM" \
  -d build/classes \
  $(find build/gen -name "*.java")

echo "=== 4b. Biên dịch mã Kotlin sang bytecode .class (kotlinc) ==="
JAVA_HOME="$JAVA_HOME" \
"$KOTLINC/bin/kotlinc" \
  -cp "$PLATFORM:$KOTLINC/lib/kotlin-stdlib.jar:$LIBS:build/classes" \
  -d build/classes \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/User.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/UserFormView.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/LoginAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/UserhomeAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/AddUserAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/ViewUserAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/EditUserAct.kt

echo "=== 5. Chuyển bytecode sang mã máy Android DEX (d8) ==="
JAVA_HOME="$JAVA_HOME" \
$BT/d8 --lib "$PLATFORM" --min-api 24 --output build/apk/ \
  $(find build/classes -name "*.class") \
  "$KOTLINC/lib/kotlin-stdlib.jar" \
  "$CL_JAR" \
  "$CL_CORE_JAR"

echo "=== 6. Đóng gói DEX vào file APK ==="
cd build/apk && zip -u ../app-unaligned.apk *.dex && cd ../..

echo "=== 7. Tối ưu hóa file APK (zipalign) ==="
$BT/zipalign -f -p 4 build/app-unaligned.apk build/app-aligned.apk

echo "=== 8. Ký số APK (apksigner) ==="
JAVA_HOME="$JAVA_HOME" \
$BT/apksigner sign --ks ~/.android/debug.keystore --ks-pass pass:android \
  --ks-key-alias androiddebugkey --key-pass pass:android \
  --out build/app-signed.apk build/app-aligned.apk

echo "=== 9. Cài đặt lên máy ảo (adb install) ==="
$SDK/platform-tools/adb install -r build/app-signed.apk

echo "=== 10. Khởi chạy ứng dụng ==="
$SDK/platform-tools/adb shell am force-stop $PKG
$SDK/platform-tools/adb shell am start -n $PKG/.LoginAct

echo "=== Hoàn tất! Ứng dụng đã chạy trên máy ảo. ==="
