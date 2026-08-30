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
    implementation(project(":core:common"))
    implementation(project(":core:design"))
    implementation(project(":core:models"))
    implementation(project(":core:navigation-api"))
    implementation(project(":core:navigation-impl"))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Data:
    implementation(project(":data:auth"))
    implementation(project(":data:image"))
    implementation(project(":data:user"))
}