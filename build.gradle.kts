// XOYZ — Single-module Android build configuration

plugins {
    id("com.android.application") version "8.2.2"
    id("org.jetbrains.kotlin.android") version "1.9.22"
}

android {
    namespace = "com.xoyz.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xoyz.game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs("src/main/kotlin")
            res.srcDirs("src/main/res")
            assets.srcDirs("src/main/assets")
        }
        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
        getByName("androidTest") {
            java.srcDirs("src/androidTest/kotlin")
        }
    }
}

dependencies {
    // AndroidX core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

     // ViewModel + LiveData (for GameViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // ──────────────────────────────────────────────────────────────────────────
    // Phase 2: LibGDX for 3D rendering (uncomment when ready)
    // ──────────────────────────────────────────────────────────────────────────

    // LibGDX (uncomment when ready for 3D rendering)
    // val gdxVersion = "1.12.1"
    // implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    // implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    // natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    // natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    // natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    // natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")

    // ──────────────────────────────────────────────────────────────────────────
    // Phase 4: Firebase for multiplayer (uncomment when ready)
    // ──────────────────────────────────────────────────────────────────────────
    
    // Firebase (uncomment when ready for multiplayer)
    // implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // implementation("com.google.firebase:firebase-database-ktx")
    // implementation("com.google.firebase:firebase-auth-ktx")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
