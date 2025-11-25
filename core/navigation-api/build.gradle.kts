plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xando.core.navigation_api"
}

dependencies {
    api(libs.kotlinx.serialization.json)
}