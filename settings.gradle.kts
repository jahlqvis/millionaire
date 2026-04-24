pluginManagement {
    repositories {
        google()
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

rootProject.name = "millionaire-tv"

include(":app-tv")

include(":core:core-ui")
include(":core:core-common")
include(":core:core-navigation")
include(":core:core-testing")
include(":core:core-audio")
include(":core:core-localization")

include(":domain:domain-model")
include(":domain:domain-usecase")

include(":data:data-repository")
include(":data:data-local-db")
include(":data:data-local-content")
include(":data:data-remote")

include(":feature:feature-home")
include(":feature:feature-player-setup")
include(":feature:feature-gameplay")
include(":feature:feature-results")
include(":feature:feature-history")
include(":feature:feature-settings")

include(":sync:sync-worker")
