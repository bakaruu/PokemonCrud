plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.pokecrud"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pokecrud"
        minSdk = 24
        targetSdk = 33
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true

    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //to use gif
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")


    //firebase
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.google.firebase:firebase-analytics:21.5.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
//    implementation ("com.iarcuschin:simpleratingbar:0.1.+")

    //swipe gestures
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

}

