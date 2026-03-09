plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.xando.feature.auth"
}

dependencies {
    // Core:
    implementation(project(":core:design"))
    implementation(project(":core:navigation-api"))

    // Navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
}