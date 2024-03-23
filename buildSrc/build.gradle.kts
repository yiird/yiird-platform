plugins {
    java
    `kotlin-dsl`
    id("org.springframework.boot") version "3.2.3" apply false
}

group = "com.yiird.platform"
version = "unspecified"

repositories {
    mavenLocal()
    maven {
        setUrl("https://maven.aliyun.com/repository/central")
    }
    maven {
        setUrl("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}