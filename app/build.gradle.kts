plugins {
    alias(libs.plugins.staya.android.appCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.kotlin.serialization)
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

    // Features:
    implementation(project(":feature:auth"))

    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}