import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.freefair.maven-optional") version "8.6"
}


version = "0.1.0"
group = "com.yiird.platform"

/*fun isModule(project: Project): Boolean {
    return !(project.name.endsWith("-starters") || project.name.endsWith("-starter"))
}*/

fun checkIsLibraryModule(project: Project): Boolean {
    return project.name.endsWith("-autoconfigure") || project.name.endsWith("-starter")
}

fun checkIsPlatformModule(project: Project): Boolean {
    return project.name.endsWith("-dependencies")
}

fun checkIsApplicationModule(project: Project): Boolean {
    return project.name.endsWith("-test")
}

fun publishConfigure(project: Project, type: String = "java") {
    project.configure<PublishingExtension> {
        publications {
            register(type, MavenPublication::class) {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(project.components[if (type == "pom") "javaPlatform" else "java"])
            }
        }
        repositories {
            mavenLocal()
        }
    }
}

fun configreVersion(project: Project) {
    project.configure<JavaPluginExtension>() {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

    if (checkIsLibraryModule(this)) {
        println("----->${this.name}")
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")
        publishConfigure(this)
        configreVersion(this)
    } else if (checkIsPlatformModule(this)) {
        apply(plugin = "java-platform")
        apply(plugin = "maven-publish")
        publishConfigure(this, "pom")
    } else if (checkIsApplicationModule(this)) {
        apply(plugin = "application")
        apply(plugin = "org.springframework.boot")
        configreVersion(this)
    }

    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.maven-optional")

    configure<StandardDependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }
}