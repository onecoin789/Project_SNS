import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleDaggerHiltAndroid)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
//    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.project_sns"
    compileSdk = 34

    val properties = Properties()
    properties.load(FileInputStream(rootProject.file("local.properties")))

    defaultConfig {
        applicationId = "com.example.project_sns"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
        buildConfigField("String", "NATIVE_KEY", properties.getProperty("NATIVE_KEY"))
        resValue("string", "NATIVE_KEY", properties.getProperty("NATIVE_KEY"))

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
}

dependencies {

    implementation(libs.glide)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // navigation fragment
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
//    implementation(libs.firebase.crashlytics)

    // retrofit
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation("com.kakao.maps.open:android:2.11.9")
    implementation("com.google.android.gms:play-services-location:16.0.0")


}