plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.data.user"
}

dependencies {
    implementation(project(":core:network"))

    implementation(libs.androidx.exifinterface)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
