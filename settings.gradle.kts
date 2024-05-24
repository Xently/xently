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

rootProject.name = "Kwanza Tukule"
include(":app-customer")
include(":app-delivery")
include(":app-sales")
include(":features:ui-core")
include(":features:authentication")
include(":features:customer-landing")
include(":features:delivery-landing")
include(":features:sales-landing")
include(":features:catalogue")
include(":libraries:pagination")
include(":features:shopping-cart")
include(":features:sales-dashboard")
include(":features:sales-customer-onboarding")
include(":features:customer-complaints")
include(":features:customer-home")
include(":libraries:data-route")
include(":libraries:data-customer")
include(":libraries:data-core")
include(":features:customer")
include(":features:route")
include(":features:missed-opportunities")
include(":features:order")
include(":libraries:location-tracker")
