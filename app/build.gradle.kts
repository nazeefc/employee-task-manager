plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "com.example.employee_task_manager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.employee_task_manager"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "SYNC_BASE_URL", "\"\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "SYNC_BASE_URL", "\"http://127.0.0.1:8080/\"")
        }
        release {
            buildConfigField("String", "SYNC_BASE_URL", "\"\"")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Retrofit & Gson Converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lifecycle ViewModel & Runtime KTX (for StateFlow and repeatOnLifecycle)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Kotlin Coroutines Android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Room Database
    val roomVersion = "2.8.5"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("androidx.test:core-ktx:1.6.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    androidTestImplementation("androidx.work:work-testing:2.9.1")
    debugImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}

tasks.register<JavaExec>("runMockServer") {
    group = "application"
    description = "Runs the local Day 11 Mock Task Server on port 8080"
    dependsOn("compileDebugKotlin")
    classpath = files(
        layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
        configurations.getByName("debugRuntimeClasspath")
    )
    mainClass.set("com.example.employee_task_manager.data.remote.LocalMockTaskServerKt")
    standardInput = System.`in`
}
