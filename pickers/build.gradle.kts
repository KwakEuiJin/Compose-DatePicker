import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)           // org.jetbrains.compose
    alias(libs.plugins.compose.compiler)  // org.jetbrains.kotlin.plugin.compose (K2)
    alias(libs.plugins.vanniktech.maven)
}

kotlin {
    // Kotlin 2.4: calling abiValidation { } enables validation; the former
    // `enabled` property was removed.
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
    }

    jvmToolchain(17)

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("17")
        }
    }

    // Compose Multiplatform 1.11 no longer publishes iosX64 (Intel Mac simulator), so exclude it.
    iosArm64()
    iosSimulatorArm64()

    jvm("desktop")

    // Library target only: no `binaries.executable()`, which belongs to applications.
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.runtime.saveable)
            api(libs.compose.foundation)
            api(libs.compose.ui)
            api(libs.kotlinx.collections.immutable)
            api(libs.kotlinx.datetime)

            // material3 supplies LocalContentColor / LocalTextStyle, which PickerDefaults reads so
            // that default picker colors and text styles follow the host MaterialTheme (including
            // dark theme). No material3 type appears in the public API, so this stays
            // `implementation`. See README "Dependencies".
            implementation(libs.compose.material3)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.kotlinx.coroutines.core)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.uitest.junit4)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.uitest.junit4)
                implementation(libs.androidx.uitest.testManifest)
                implementation(libs.robolectric)
            }
        }
    }
}

dependencies {
    // Tooling that must never reach consumers of the published release artifact: the Android
    // Studio preview renderer and the test manifest are debug-only.
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.uitest.testManifest)
}

android {
    namespace = "io.github.kezlab.compose.pickers"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }

        managedDevices {
            localDevices {
                create("pixel2Api35") {
                    device = "Pixel 2"
                    apiLevel = 35
                    systemImageSource = "aosp-atd"
                    require64Bit = true
                }
            }
        }
    }

    buildFeatures { compose = true }
}
