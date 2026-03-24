plugins {
    alias(libs.plugins.staya.android.baseCfg)
    id("kotlin-parcelize")
}

android {
    namespace = "com.xando.core.models"
}

dependencies {
    api(project(":core:api-models"))
}