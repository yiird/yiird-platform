import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.freefair.maven-optional") version "8.6"
}


val isCI = System.getenv("CI")
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

            if (!"true".equals(isCI)) {
                mavenLocal()
            }else{
                val isAlpha = project.version.toString().endsWith("-alpha");
                maven {
                    name = "Nexus"
                    url = uri(if (isAlpha) "http://47.104.74.142:8081/repository/yiird-snapshot/" else "http://47.104.74.142:8081/repository/yiird-release/")
                    credentials {
                        username = System.getenv("MAVEN_USERNAME")
                        password = System.getenv("MAVEN_PASSWORD")
                    }
                }
            }
        }
    }
}

fun configreVersion(project: Project) {
    project.configure<JavaPluginExtension>() {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

allprojects {
    if ("true".equals(isCI)) {
        allprojects {
            //配置全局依赖仓库
            repositories {
                mavenCentral()
            }
        }
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