plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val lesson = providers.gradleProperty("lesson").orElse("bai07").get()
require(lesson in setOf("bai03", "bai04", "bai05", "bai06", "bai07")) {
    "Unsupported lesson: $lesson"
}

android {
    namespace = "ptit.cnpm1.tranxuanthanh.$lesson"
    compileSdk = 35

    defaultConfig {
        applicationId = "ptit.cnpm1.tranxuanthanh.$lesson"
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
    // Mỗi bài có package, manifest, Kotlin và resources riêng.
    // Chọn bài bằng ./gradlew -Plesson=bai07 :app:assembleDebug.
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest-$lesson.xml")
            java.setSrcDirs(listOf("src/main/java/ptit/cnpm1/tranxuanthanh/$lesson"))
            res.setSrcDirs(listOf("src/main/res-$lesson"))
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
