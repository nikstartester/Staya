plugins {
    `kotlin-dsl`
}

group = "com.xando.gradleSettings.convention"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}