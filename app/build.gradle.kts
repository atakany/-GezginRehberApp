plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt") 
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.example.gezginrehberbt"
    compileSdk = 33
    
    // AAR metadata uyarılarını önle
    configurations.all {
        resolutionStrategy {
            // Aynı modülün farklı sürümlerini zorla
            force("androidx.core:core-ktx:1.10.1")
            force("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
            force("androidx.appcompat:appcompat:1.6.1")
            force("com.google.android.material:material:1.9.0")
            force("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.0")
            force("androidx.collection:collection:1.2.0")
            force("androidx.fragment:fragment-ktx:1.6.1")
            force("androidx.activity:activity-ktx:1.7.2")
            force("com.google.firebase:firebase-common:20.3.0")
            force("androidx.navigation:navigation-fragment-ktx:2.5.3")
            force("androidx.navigation:navigation-ui-ktx:2.5.3")
            force("androidx.emoji2:emoji2:1.3.0")
            force("androidx.emoji2:emoji2-views-helper:1.3.0")
            force("androidx.activity:activity:1.7.2")
            force("androidx.core:core:1.10.1")
            force("androidx.test:core:1.5.0")
            force("androidx.test:runner:1.5.0")
            force("androidx.test.ext:junit:1.1.5")
            
            // Çakışan bağımlılıkları çöz
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
            
            // Aynı modülün farklı sürümlerini seç
            preferProjectModules()
        }
    }
    
    // Java 11 özelliklerini etkinleştir
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    
    // Kotlin derleyici hedeflerini ayarla
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn") 
    }

    defaultConfig {
        applicationId = "com.example.gezginrehberbt"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        // Standart test runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Google Maps API Key
        val mapsApiKey: String = project.findProperty("MAPS_API_KEY") as? String ?: System.getenv("MAPS_API_KEY") ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        
        // Room Schema
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
        
        // Çoklu DEX desteği
        multiDexEnabled = true
        
        // Google Play Services Version
        buildConfigField("int", "GOOGLE_PLAY_SERVICES_VERSION", "12451000")
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module",
                "META-INF/versions/9/previous-compilation-data.bin"
            )
            pickFirsts += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/proguard/androidx-annotations.pro",
                "META-INF/proguard/coroutines.pro"
            )
        }
    }
    
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    
    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }
    
    // KSP ve KAPT için derleyici argümanları - bu, Hilt'in düzgün çalışmasını sağlar
    kapt {
        correctErrorTypes = true
        arguments {
            arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        }
    }
}

dependencies {
    // Core Android Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.multidex:multidex:2.0.1")
    
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    
    // Room components
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    
    // Dagger Hilt - ksp yerine kapt kullanıyoruz, daha güvenilir çalışacak
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    
    // Firebase - Belirli sürümler kullanarak
    implementation(platform("com.google.firebase:firebase-bom:31.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.4.3")
    implementation("com.google.firebase:firebase-storage-ktx:20.1.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.3")
    implementation("com.google.firebase:firebase-perf-ktx:20.3.1")
    implementation("com.google.firebase:firebase-config-ktx:21.2.1")
    implementation("com.google.firebase:firebase-common-ktx:20.3.0")
    
    // Google Play Services
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.1.0")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    ksp("com.github.bumptech.glide:ksp:4.15.1")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    
    // Jetpack Compose
    val composeVersion = "1.4.0"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    
    // Other Libraries
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    
    // Debug Only
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")
    debugImplementation("com.facebook.stetho:stetho:1.6.0")
    debugImplementation("com.facebook.stetho:stetho-okhttp3:1.6.0")
    
    // Unit Test Bağımlılıkları
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.44")
    kaptTest("com.google.dagger:hilt-compiler:2.44")

    // Android Test Bağımlılıkları
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.44")
    
    // Fragment Testing
    debugImplementation("androidx.fragment:fragment-testing:1.6.1")
}

// KSP configuration for Room
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}