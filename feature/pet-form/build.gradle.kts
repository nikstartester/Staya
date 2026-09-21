plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.xando.feature.pet_form"
}

dependencies {
    // Core:
    implementation(project(":core:design"))
    implementation(project(":core:models"))
    implementation(project(":core:navigation-api"))
    implementation(project(":core:pet-dictionary"))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Data:
    implementation(project(":data:pet"))
    implementation(project(":data:image"))
}
