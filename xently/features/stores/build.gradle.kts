plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "co.ke.xently.features.stores"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.android.jvm.target.get()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.ui)
    implementation(libs.bundles.ui.navigation)
    implementation(libs.chip.text.field)

    implementation(libs.hilt.android)
    implementation(project(":xently:features:qrcode"))
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.ktor)
    implementation(libs.timber)
    implementation(libs.date.time)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":xently:features:ui-core"))
    implementation(project(":xently:features:shops"))
    implementation(project(":xently:features:location-picker"))
    implementation(project(":xently:features:storecategory"))
    implementation(project(":xently:features:storeservice"))
    implementation(project(":xently:features:openinghours"))
    implementation(project(":xently:libraries:ui-pagination"))
    api(project(":xently:features:stores-data"))

    debugImplementation(libs.bundles.ui.debug)

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}