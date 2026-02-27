rootProject.name = "cc3d"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
        //google {
        //    mavenContent {
        //        includeGroupAndSubgroups("androidx")
        //        includeGroupAndSubgroups("com.android")
        //        includeGroupAndSubgroups("com.google")
        //    }
        //}
        mavenCentral()
        maven("https://jitpack.io")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")