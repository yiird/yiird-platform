import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

fun RepositoryHandler.mavenCustom() = run {

    val isCI = System.getenv("CI")

    if ("true" !== isCI) {
        maven {
            setUrl("https://maven.aliyun.com/repository/central")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
    }

    mavenCentral()
}


fun Project.configPlugins() = run {

    if (isLibraryModule()) {
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")
    } else if (isPlatformModule()) {
        apply(plugin = "java-platform")
        apply(plugin = "maven-publish")
    } else if (isApplicationModule()) {
        apply(plugin = "application")
    }

    if(isLibraryModule() || isApplicationModule()){
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "io.freefair.maven-optional")

        dependencies {
            add("optional", "org.springframework.boot:spring-boot-configuration-processor:${Versions.SPRING_BOOT}")
        }
    }
}

fun Project.configJavaVersion() = run {
    if (plugins.hasPlugin("java")) {
        configure<JavaPluginExtension>() {
            sourceCompatibility = Versions.JAVA_SOURCE
            targetCompatibility = Versions.JAVA_SOURCE
        }
    }
}

fun Project.isLibraryModule() = run {
    name.endsWith("-autoconfigure") || name.endsWith("-starter")
}

fun Project.isPlatformModule() = run {
    name.endsWith("-dependencies")
}

fun Project.isApplicationModule() = run {
    name.endsWith("-test")
}

fun Project.configLombok() = run {
    if (isLibraryModule() || isApplicationModule()) {
        dependencies {
            add("optional", "org.projectlombok:lombok:${Versions.LOMBOK}")
            add("annotationProcessor", "org.projectlombok:lombok:${Versions.LOMBOK}")
            add("testAnnotationProcessor", "org.projectlombok:lombok:${Versions.LOMBOK}")
        }
    }
}

fun Project.configTest() = run {
    if (isLibraryModule() || isApplicationModule()) {
        dependencies {
            add("testImplementation", "org.springframework.boot:spring-boot-starter-test:${Versions.SPRING_BOOT}")
            add("testImplementation", "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT}")
        }
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}