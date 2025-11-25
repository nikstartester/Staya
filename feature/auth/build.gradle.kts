plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.staya.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xando.auth"
}

dependencies {
    implementation(project(":design"))

    implementation(libs.androidx.navigation.compose)
}