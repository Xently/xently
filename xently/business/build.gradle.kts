import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    alias(libs.plugins.secrets)
}

secrets {
    defaultPropertiesFileName = "secrets.default.properties"
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}

val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(rootProject.file("secrets/keystore.properties")))

android {
    signingConfigs {
        create("config") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
        }
    }

    namespace = "co.ke.xently.business"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "co.ke.xently.business"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
        versionCode = libs.versions.android.version.code.get().toInt()
        versionName = libs.versions.android.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("config")
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
    implementation(libs.adaptive.navigation)

    implementation(libs.hilt.android)
    implementation(project(":xently:features:reviewcategory"))
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.ktor)
    implementation(libs.timber)
    implementation(libs.date.time)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":xently:features:ui-core"))
    implementation(project(":xently:features:auth"))
    implementation(project(":xently:features:customers"))
    implementation(project(":xently:features:merchant"))
    implementation(project(":xently:features:notifications"))
    implementation(project(":xently:features:notification-topic"))
    implementation(project(":xently:features:products"))
    implementation(project(":xently:features:profile"))
    implementation(project(":xently:features:qrcode"))
    implementation(project(":xently:features:reviews"))
    implementation(project(":xently:features:settings"))
    implementation(project(":xently:features:shops"))
    implementation(project(":xently:features:stores"))

    debugImplementation(libs.bundles.ui.debug)

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}