plugins {
    id("android-base-plugin")
}

android {
    namespace = "com.xando.auth"
}

dependencies {
    implementation(project(":design"))
}