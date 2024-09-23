/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
//    id(libs.plugins.jetbrains.dokka.get().pluginId)
//    id(libs.plugins.gradle.metalava.get().pluginId)
//    id(libs.plugins.vanniktech.maven.publish.get().pluginId)
}

kotlin {
    explicitApi()
}

android {
    namespace = "com.google.accompanist.placeholder.material3"

    compileSdk = 34

    defaultConfig {
        minSdk = 21
        // targetSdkVersion has no effect for libraries. This is only used for the test APK
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
        targetCompatibility = JavaVersion.valueOf(libs.versions.android.jvm.compatibility.get())
    }

    buildFeatures {
        buildConfig = false
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    /*lint {
        textReport = true
        textOutput = File("stdout")
        // We run a full lint analysis as build part in CI, so skip vital checks for assemble tasks
        checkReleaseBuilds = false
        disable += setOf("GradleOverrides")
    }*/

    packaging {
        // Some of the META-INF files conflict with coroutines-test. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        resources {
            excludes += listOf("/META-INF/AL2.0", "/META-INF/LGPL2.1")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }
}

/*metalava {
    sourcePaths.setFrom("src/main")
    filename.set("api/current.api")
    reportLintsAsErrors.set(true)
}*/

dependencies {
    implementation(libs.androidx.material3)
    api(project(":xently:libraries:placeholder"))
    implementation(libs.kotlin.coroutines.android)
}