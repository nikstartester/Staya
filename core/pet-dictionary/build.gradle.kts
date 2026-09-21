plugins {
    alias(libs.plugins.staya.android.baseCfg)
}

android {
    namespace = "com.xando.core.pet_dictionary"
}

dependencies {
    api(project(":core:models"))

    implementation(libs.androidx.core.ktx)
}
