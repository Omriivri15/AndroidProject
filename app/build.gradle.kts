plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // Firebase plugin
    id("kotlin-parcelize")

}

val MAPS_API_KEY: String = project.findProperty("MAPS_API_KEY") as? String ?: ""


android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "google_maps_key", MAPS_API_KEY)

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database.ktx)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.cloudinary:cloudinary-android:2.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")  // גרסה עדכנית


    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")


    implementation ("androidx.activity:activity-ktx:1.7.2") // Use the latest version
    implementation ("androidx.fragment:fragment-ktx:1.5.7") // Required for fragments

    // Firebase platform for managing versions
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    // Firebase dependencies
    implementation("com.google.firebase:firebase-storage-ktx") // Firebase Storage
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication

    // Picasso for image loading
    implementation ("com.squareup.picasso:picasso:2.71828")

    // Glide for image loading
    implementation ("com.github.bumptech.glide:glide:4.13.2") // Glide dependency
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.2") // Glide annotation processor
}
