plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xando.feature.auth"
}

dependencies {
    // Core:
    implementation(project(":core:design"))
    implementation(project(":core:navigation-api"))

    implementation(libs.androidx.navigation.compose)
}