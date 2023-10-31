plugins{
    id("com.android.application")
    id("kotlin-android")                    // kotlin("android")
    id("kotlin-parcelize")                  /*https://developer.android.com/topic/libraries/view-binding/migration#kts*/
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "ua.co.myrecipes"
        minSdk = 22
        targetSdk = 33
        versionCode = 11
        versionName = "2.0.0"
        namespace = "ua.co.myrecipes"

        //testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "ua.co.myrecipes.HiltTestRunner"
    }

    buildTypes {
        getByName("debug"){
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("release"){
            isMinifyEnabled = true
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    /*For testing*/
    packagingOptions {
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/**")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
    }
    buildFeatures {
        viewBinding = true
    }
    bundle {
        language {
            // Specifies that the app bundle should not support
            // configuration APKs for language resources. These
            // resources are instead packaged with each base and
            // dynamic feature APK.
            enableSplit = false
        }
    }
}

dependencies {
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-compiler:2.46")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Navigation Components
    val navComponents = "2.5.3"
    implementation("androidx.navigation:navigation-fragment-ktx:$navComponents")
    implementation("androidx.navigation:navigation-ui-ktx:$navComponents")

    // Activity KTX for viewModels(), ActivityResultContracts
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.5.7")

    // Local Unit Tests
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("com.google.truth:truth:1.1.3")

    // Instrumented Unit Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation("com.google.truth:truth:1.1.3")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.46")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.12.1")
    androidTestImplementation("org.mockito:mockito-core:4.6.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.46")
    debugImplementation("androidx.fragment:fragment-testing:1.5.7")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx")

    //Retrofit
    val retrofit = "2.9.0"
    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit")

    // Easy Permissions
    implementation ("pub.devrel:easypermissions:3.0.0")

    //Image cropper
    implementation ("com.github.CanHub:Android-Image-Cropper:4.2.1")

    //CircleImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    kapt ("com.github.bumptech.glide:compiler:4.12.0")

    //LeakCanary
    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.7")
}

tasks.create("printVersionName") {
    val versionName = android.defaultConfig.versionName
    println("Version name: $versionName")
}
