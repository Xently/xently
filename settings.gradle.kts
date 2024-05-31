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
include(":kwanza-tukule:customer")
include(":kwanza-tukule:delivery")
include(":kwanza-tukule:sales")
include(":kwanza-tukule:features:ui-core")
include(":kwanza-tukule:features:authentication")
include(":kwanza-tukule:features:customer-landing")
include(":kwanza-tukule:features:delivery-landing")
include(":kwanza-tukule:features:sales-landing")
include(":kwanza-tukule:features:catalogue")
include(":kwanza-tukule:features:shopping-cart")
include(":kwanza-tukule:features:sales-dashboard")
include(":kwanza-tukule:features:sales-customer-onboarding")
include(":kwanza-tukule:features:customer-complaints")
include(":kwanza-tukule:features:customer-home")
include(":kwanza-tukule:libraries:data-route")
include(":kwanza-tukule:libraries:data-customer")
include(":kwanza-tukule:features:customer")
include(":kwanza-tukule:features:route")
include(":kwanza-tukule:features:missed-opportunities")
include(":kwanza-tukule:features:order")
include(":kwanza-tukule:features:delivery-dispatch")
include(":kwanza-tukule:features:delivery-route")
include(":kwanza-tukule:features:delivery-profile")
include(":kwanza-tukule:features:delivery-home")

include(":xently:business")
include(":xently:customer")
include(":xently:libraries:pagination")
include(":xently:libraries:location-tracker")
include(":xently:libraries:data-auth")
include(":xently:libraries:data-network")
include(":xently:libraries:data-local")
include(":xently:features:shops")
include(":xently:libraries:data-core")
include(":xently:libraries:ui-image")
include(":xently:libraries:ui-core")
