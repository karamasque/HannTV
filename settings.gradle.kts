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
        // tv.own.owntv:core and :player-core, built from https://github.com/ahXN00/OwnTV_Core.
        // That repository is public, but GitHub's Maven registry demands credentials even for a
        // public package — so resolution needs a token with read:packages. Put it in
        // ~/.gradle/gradle.properties as gpr.user / gpr.token, NEVER in this repo. CI passes the
        // same values through the GITHUB_ACTOR / GPR_TOKEN environment variables, except on a fork
        // pull request, where GitHub withholds secrets and CI builds core from source instead.
        maven {
            name = "OwnTVCore"
            url = uri("https://maven.pkg.github.com/ahXN00/OwnTV_Core")
            credentials {
                username = providers.gradleProperty("gpr.user")
                    .orElse(providers.environmentVariable("GITHUB_ACTOR")).orNull
                password = providers.gradleProperty("gpr.token")
                    .orElse(providers.environmentVariable("GPR_TOKEN")).orNull
            }
            content { includeGroup("tv.own.owntv") }
        }
    }
}

// Local development: build against core's own source instead of the published artifact, so a core
// edit reaches this app with no publish step. Gradle substitutes the dependency automatically
// because OwnTV_Core publishes under the same group and artifact ids this app asks for. CI leaves
// owntv.corePath unset and resolves the pinned version instead.
rootProject.name = "HanTV"
include(":app")
include(":core")
include(":player-core")
include(":baselineprofile")
 