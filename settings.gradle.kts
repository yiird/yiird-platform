plugins {
    id("com.mooltiverse.oss.nyx") version "2.5.2"
}

rootProject.name = "yiird-platform"

include("yiird-spring-boot-autoconfigure")
include("yiird-spring-boot-starters")
include("yiird-spring-boot-starters:yiird-data-spring-boot-starter")
include("yiird-spring-boot-starters:yiird-json-spring-boot-starter")
include("yiird-spring-boot-starters:yiird-security-spring-boot-starter")
include("yiird-dependencies")
include("yiird-tests")
include("yiird-tests:yiird-data-test")
include("yiird-tests:yiird-json-test")