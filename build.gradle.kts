import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version Versions.SPRING_BOOT apply false
    id("io.freefair.maven-optional") version "8.6"
}

version = "0.1.0"
group = "com.yiird.platform"


allprojects {
    repositories {
        mavenCustom()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    configurations {
        all {
            exclude("com.zaxxer", "HikariCP")
        }
    }

    configPlugins()
    configJavaVersion()
    configLombok()
    configTest()
    if (isLibraryModule() || isApplicationModule()) {
        configure<StandardDependencyManagementExtension> {
            imports {
                mavenBom(SpringBootPlugin.BOM_COORDINATES)
            }
        }
    }

    configPublish()

    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }
}