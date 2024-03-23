import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

fun PublishingExtension.createPublication(project: Project, type: String) = run {
    publications {
        println("project:${project},version:${project.version}")
        register(type, MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(project.components[if (type == "pom") "javaPlatform" else "java"])
        }
    }

}


fun PublishingExtension.createRepository(isAlpha: Boolean) {
    val isCI = System.getenv("CI")
    repositories {
        if ("true" != isCI) {
            mavenLocal()
        } else {
            maven {
                name = "Nexus"
                setUrl(if (isAlpha) "http://47.104.74.142:8081/repository/yiird-snapshot/" else "http://47.104.74.142:8081/repository/yiird-release/")
                isAllowInsecureProtocol = true;

                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}

fun Project.configPublish() = run {

    if (isApplicationModule()) return;
    val isAlpha = version.toString().endsWith("-alpha")
    if (isPlatformModule()) {
        configure<PublishingExtension> {
            createPublication(project, "pom")
            createRepository(isAlpha)
        }
    } else if (isLibraryModule()) {
        configure<PublishingExtension> {
            createPublication(project, "java")
            createRepository(isAlpha)
        }
    }
}
