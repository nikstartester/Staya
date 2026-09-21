plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xando.core.navigation_api"
}

dependencies {
    api(project(":core:models"))

    api(libs.kotlinx.serialization.json)

    api(libs.androidx.navigation3.runtime)
}