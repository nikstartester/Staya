plugins {
    alias(libs.plugins.staya.android.baseCfg)
}

android {
    namespace = "com.xando.domain.image"
}

dependencies {
    // Data:
    api(project(":data:image"))
}
