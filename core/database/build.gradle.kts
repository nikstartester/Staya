plugins {
    alias(libs.plugins.staya.android.baseCfg)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
}

android {
    namespace = "com.xando.core.database"
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Room
    api(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.framework)
    ksp(libs.androidx.room3.compiler)
}
