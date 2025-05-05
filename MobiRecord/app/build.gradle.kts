plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
     id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
 }

android {
    namespace = "com.mobivault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mobivault"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding= true
        buildConfig=true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "SERVER_URL",
                value = "\"http://35.154.175.30/api/\""
            );
        }
        debug {


            buildConfigField(
                "String",
                "SERVER_URL",
                value = "\"http://35.154.175.30/api/\""
            );

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewbinding)
    implementation(libs.androidx.databinding.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //dagger:hilt
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-android-compiler:2.46")

    //lottie
    val lottieVersion = "6.5.2"
    implementation("com.airbnb.android:lottie:$lottieVersion")

    //Lifecycle
    var lifecycle_version = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    // optional - helpers for implementing LifecycleOwner in a Service
    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")

    //Navigation
    var nav_version = "2.5.3"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //okhttp3
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    //Kotlin Coroutines
    val coroutines_android_version = "1.5.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_android_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_android_version")

    //glide dependency
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //custom camera
    implementation ("com.otaliastudios:cameraview:2.7.2")
     api ("com.otaliastudios:cameraview:2.7.2")
    //pinview for otp
    implementation("io.github.chaosleung:pinview:1.4.4")
    //Android12 Splash
    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.tbuonomo:dotsindicator:4.3")


}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
