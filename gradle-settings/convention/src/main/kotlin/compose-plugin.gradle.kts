import com.android.build.api.dsl.CommonExtension

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

val android = extensions.getByName("android") as CommonExtension<*, *, *, *, *, *>

android.buildFeatures {
    compose = true
}