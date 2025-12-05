plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "app"

include(":main")
include(":api")
include(":events-contract")
include(":audit-service")
include("statistics-service")
include("grpc-server")
include("grpc-client")
include("analytics-service")
