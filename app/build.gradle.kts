plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ptit.cnpm1.tranxuanthanh.bai05"
    compileSdk = 35

    defaultConfig {
        applicationId = "ptit.cnpm1.tranxuanthanh.bai05"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    // Mỗi bài nằm trong một package + thư mục res riêng. Gradle chỉ dựng được
    // một namespace nên ở đây trỏ vào bài 5; muốn dựng bài khác thì chạy
    // ./build_and_run.sh bai03|bai04
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest-bai05.xml")
            java.setSrcDirs(listOf("src/main/java/ptit/cnpm1/tranxuanthanh/bai05"))
            res.setSrcDirs(listOf("src/main/res-bai05"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
}
