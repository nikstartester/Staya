plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.data.pet"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:models"))
    implementation(project(":core:network"))

    // Data:
    implementation(project(":data:image"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
