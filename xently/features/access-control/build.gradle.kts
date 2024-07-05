plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "co.ke.xently.features.access.control"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                """"https://7706-105-163-2-136.ngrok-free.app/api/v1"""",
            )
        }
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "BASE_URL",
                """"https://api.xently.co.ke/api/v1"""",
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.android.jvm.target.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.ktor)
    implementation(libs.timber)
    implementation(libs.date.time)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":xently:libraries:data-core"))

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.junit)
}