import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.android.mapsplatform.secretsGradlePlugin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.parcelize)
}

// ✔️ Leer archivo secrets.properties
secrets {
    propertiesFileName = "secrets.properties"
}

// 🔴 cargar keystore
val keystoreFile = rootProject.file("keystore.properties")
val keystore = Properties()
if (keystoreFile.exists()) {
    keystore.load(FileInputStream(keystoreFile))
}

android {
    namespace = "com.upd.kvupd"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.upd.kvupd"
        minSdk = 24
        targetSdk = 34
        versionCode = 240010511
        versionName = "1.5.11"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystore["storeFile"] as String)
            storePassword = keystore["storePassword"] as String
            keyAlias = keystore["keyAlias"] as String
            keyPassword = keystore["keyPassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.bundles.feature.coroutines)
    implementation(libs.bundles.feature.play.services)
    implementation(libs.bundles.feature.lifecycle)
    implementation(libs.flexbox)
    implementation(libs.worker)
    implementation(libs.bundles.feature.navigation)
    implementation(libs.bundles.feature.room)
    ksp(libs.room.compiler)
    implementation(libs.hilt.work)
    kapt(libs.hilt.compiler)
    implementation(libs.bundles.feature.retrofit)
    implementation(libs.bundles.feature.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.bundles.feature.okhttp)
    implementation(libs.zxing)
    implementation(libs.dialogs)
    implementation(libs.glide)
    ksp(libs.glide.compiler)
    implementation(libs.socket) { exclude(group = "org.json", module = "json") }

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}