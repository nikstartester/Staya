plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.data.image"
}

dependencies {
    implementation(project(":core:api-models"))

    implementation(libs.androidx.exifinterface)

    // WorkManager
    api(libs.androidx.work.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
