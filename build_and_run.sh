#!/bin/bash
set -e

echo "=== 1. Chuẩn bị thư mục build ==="
rm -rf build
mkdir -p build/compiled_res build/gen build/classes build/apk

echo "=== 2. Biên dịch giao diện XML (aapt2 compile) ==="
$HOME/Library/Android/sdk/build-tools/36.0.0/aapt2 compile --dir app/src/main/res -o build/compiled_res.zip

echo "=== 3. Liên kết tài nguyên và sinh file R.java (aapt2 link) ==="
$HOME/Library/Android/sdk/build-tools/36.0.0/aapt2 link -I $HOME/Library/Android/sdk/platforms/android-37.0/android.jar \
  --manifest app/src/main/AndroidManifest.xml \
  --min-sdk-version 24 \
  --target-sdk-version 35 \
  --java build/gen \
  -o build/app-unaligned.apk \
  build/compiled_res.zip \
  --auto-add-overlay

echo "=== 4. Biên dịch mã Kotlin sang bytecode .class (kotlinc) ==="
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
"/Applications/Android Studio.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc" \
  -cp "$HOME/Library/Android/sdk/platforms/android-37.0/android.jar:/Applications/Android Studio.app/Contents/plugins/Kotlin/kotlinc/lib/kotlin-stdlib.jar" \
  -d build/classes \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/User.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/LoginAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/UserhomeAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/AddUserAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/ViewUserAct.kt \
  app/src/main/java/ptit/cnpm1/tranxuanthanh/bai03/EditUserAct.kt \
  build/gen/ptit/cnpm1/tranxuanthanh/bai03/R.java

echo "=== 5. Chuyển bytecode sang mã máy Android DEX (d8) ==="
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
$HOME/Library/Android/sdk/build-tools/36.0.0/d8 --lib "$HOME/Library/Android/sdk/platforms/android-37.0/android.jar" --output build/apk/ $(find build/classes -name "*.class") "/Applications/Android Studio.app/Contents/plugins/Kotlin/kotlinc/lib/kotlin-stdlib.jar"

echo "=== 6. Đóng gói DEX vào file APK ==="
cd build/apk && zip -u ../app-unaligned.apk *.dex && cd ../..

echo "=== 7. Tối ưu hóa file APK (zipalign) ==="
$HOME/Library/Android/sdk/build-tools/36.0.0/zipalign -f -p 4 build/app-unaligned.apk build/app-aligned.apk

echo "=== 8. Ký số APK (apksigner) ==="
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
$HOME/Library/Android/sdk/build-tools/36.0.0/apksigner sign --ks ~/.android/debug.keystore --ks-pass pass:android --ks-key-alias androiddebugkey --key-pass pass:android --out build/app-signed.apk build/app-aligned.apk

echo "=== 9. Cài đặt lên máy ảo (adb install) ==="
$HOME/Library/Android/sdk/platform-tools/adb install -r build/app-signed.apk

echo "=== 10. Khởi chạy ứng dụng ==="
$HOME/Library/Android/sdk/platform-tools/adb shell am force-stop ptit.cnpm1.tranxuanthanh.bai03
$HOME/Library/Android/sdk/platform-tools/adb shell am start -n ptit.cnpm1.tranxuanthanh.bai03/.LoginAct

echo "=== Hoàn tất! Ứng dụng đã chạy trên máy ảo. ==="
