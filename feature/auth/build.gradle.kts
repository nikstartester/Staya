plugins {
    id("android-base-plugin")
    id("compose-plugin")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xando.auth"
}

dependencies {
    implementation(project(":design"))
}