pluginManagement {
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NewsApp"

// 启用类型安全的项目访问器
// 这允许使用projects.feature.foryou这样的语法访问项目
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":feature")
include(":core")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:ui")
include(":feature:home")
include(":feature:search")
include(":feature:profile")
include(":feature:interests")
include(":feature:newsdetail")
include(":feature:auth")
include(":feature:bookmarks")
