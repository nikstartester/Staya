plugins {
    alias(libs.plugins.staya.android.appCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.staya"

    defaultConfig {
        applicationId = "com.xando.staya"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Core:
    implementation(project(":core:design"))
    implementation(project(":core:navigation-api"))
    implementation(project(":core:navigation-impl"))

    // Data:
    implementation(project(":data:auth"))

    // Features:
    implementation(project(":feature:auth"))

    // Splash screen
    implementation(libs.androidx.core.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}