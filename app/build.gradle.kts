plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.android.mapsplatform.secretsGradlePlugin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.upd.kvupd"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.upd.kvupd"
        minSdk = 24
        targetSdk = 35
        versionCode = 240010600
        versionName = "1.6.0"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //ruta para schemas room
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.feature.ui)
    implementation(libs.bundles.feature.coroutines)
    implementation(libs.bundles.feature.play.services)
    implementation(libs.bundles.feature.lifecycle)
    implementation(libs.bundles.feature.navigation)
    implementation(libs.bundles.feature.room)
    implementation(libs.bundles.feature.retrofit)
    implementation(libs.bundles.feature.moshi)
    implementation(libs.bundles.feature.okhttp)
    implementation(libs.bundles.feature.firebase)
    implementation(libs.dagger.hilt)
    implementation(libs.worker)
    implementation(libs.hilt.work)
    implementation(libs.glide)
    implementation(libs.zxing)
    implementation(libs.socket) { exclude(group = "org.json", module = "json") }

    kapt(libs.dagger.hilt.compiler)
    ksp(libs.room.compiler)
    kapt(libs.hilt.compiler)
    ksp(libs.moshi.codegen)
    ksp(libs.glide.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}