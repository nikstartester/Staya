plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xando.data.user"
}

dependencies {
    implementation(project(":core:api-models"))
    implementation(project(":core:network"))

    implementation(libs.androidx.exifinterface)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
