import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

val signingProperties = Properties().apply {
    val file = rootProject.file("signing.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

val normalizedSigningProperties = signingProperties.entries.associate { (key, value) ->
    key.toString()
        .removePrefix("\uFEFF")
        .removePrefix("\u00EF\u00BB\u00BF") to value.toString()
}

fun signingProperty(name: String): String? =
    normalizedSigningProperties[name] ?: System.getenv(name)

val releaseStoreFile = signingProperty("EDGEGESTURE_STORE_FILE")?.let(rootProject::file)
val hasReleaseSigning = releaseStoreFile?.exists() == true &&
        signingProperty("EDGEGESTURE_STORE_PASSWORD") != null &&
        signingProperty("EDGEGESTURE_KEY_ALIAS") != null &&
        signingProperty("EDGEGESTURE_KEY_PASSWORD") != null

android {
    namespace = "com.example.myedgegesture"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myedgegesture"
        minSdk = 26  // Android 8.0 - 支持更多设备
        targetSdk = 35
        versionCode = 15
        versionName = "1.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("releaseLocal") {
                storeFile = releaseStoreFile
                storePassword = signingProperty("EDGEGESTURE_STORE_PASSWORD")
                keyAlias = signingProperty("EDGEGESTURE_KEY_ALIAS")
                keyPassword = signingProperty("EDGEGESTURE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = if (hasReleaseSigning) {
                signingConfigs.getByName("releaseLocal")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
    }

    applicationVariants.all {
        outputs.all {
            val variantOutput = this as BaseVariantOutputImpl
            variantOutput.outputFileName = "EdgeGesture-v$versionName.apk"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Architecture components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.org.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockk.android)

    compileOnly("de.robv.android.xposed:api:82")
}

// Detekt configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt.yml")
}

// Ktlint configuration
ktlint {
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
