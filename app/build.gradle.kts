import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.android.serialconnect"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.android.serialconnect"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

/**
 * Provider for the app version name.
 * Fallbacks to "0.0.0" if versionName is not set.
 */
val vNameProvider = provider { android.defaultConfig.versionName ?: "0.0.0" }

/**
 * Helper function to generate human-readable date for folder naming.
 * Format: Sep_04_2025
 */
fun getDateFolderName(): String {
    val sdf = SimpleDateFormat("MMM_dd_yyyy") // Month (short), day, year
    return sdf.format(Date())
}

/**
 * Task to export the Debug APK.
 * - Depends on `assembleDebug`.
 * - Copies the APK into a folder named by the current date.
 * - Renames the APK to include the version only.
 */
tasks.register<Copy>("exportDebugApk") {
    dependsOn("assembleDebug")

    val vName = vNameProvider.get()
    val dateFolder = getDateFolderName()
    val apkDir = layout.buildDirectory.dir("outputs/apk/debug")

    from(apkDir.map { it.file("app-debug.apk") })
    into(layout.projectDirectory.dir("releases").dir(dateFolder)) // Folder by date
    rename { "app-debug-v${vName}.apk" } // APK filename includes version only
}

/**
 * Task to export the Release APK.
 * - Depends on `assembleRelease`.
 * - Copies the APK into a folder named by the current date.
 * - Renames the APK to include the version only.
 */
tasks.register<Copy>("exportReleaseApk") {
    dependsOn("assembleRelease")

    val vName = vNameProvider.get()
    val dateFolder = getDateFolderName()
    val apkDir = layout.buildDirectory.dir("outputs/apk/release")

    from(apkDir.map { it.file("app-release.apk") })
    into(layout.projectDirectory.dir("releases").dir(dateFolder)) // Folder by date
    rename { "app-release-v${vName}.apk" } // APK filename includes version only
}