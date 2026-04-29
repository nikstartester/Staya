plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.data.pet_list"
}

dependencies {
    implementation(project(":core:models"))
    implementation(project(":core:network"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
