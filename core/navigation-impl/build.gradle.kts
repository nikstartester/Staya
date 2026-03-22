plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.staya.compose)
}

android {
    namespace = "com.xando.core.navigation_impl"
}

dependencies {
    implementation(project(":core:navigation-api"))

    api(libs.androidx.navigation3.ui)
    api(libs.androidx.lifecycle.viewmodel.navigation3)
}