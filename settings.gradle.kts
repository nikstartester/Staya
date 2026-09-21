pluginManagement {
    includeBuild("gradle-settings")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Staya"

include(":app")

//Core:
include(":core:api-models")
include(":core:common")
include(":core:database")
include(":core:design")
include(":core:models")
include(":core:navigation-api")
include(":core:navigation-impl")
include(":core:network")
include(":core:pet-dictionary")

//Data:
include(":data:auth")
include(":data:image")
include(":data:pet")
include(":data:user")

//Domain:
include(":domain:image")

//Features:
include(":feature:auth")
include(":feature:pet-breed-picker")
include(":feature:pet-form")
include(":feature:pet-interests-picker")
include(":feature:pet-list")
