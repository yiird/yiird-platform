javaPlatform {
    allowDependencies()
}

object Versions {
    const val DRUID = "1.2.22"
    const val MYSQL = "8.3.0"
    const val ATOMIKOS = "6.0.0"
}

dependencies {
    constraints {
        api("com.alibaba:druid:${Versions.DRUID}")
        api("com.mysql:mysql-connector-j:${Versions.MYSQL}")
        api("com.atomikos:transactions-spring-boot3-starter:${Versions.ATOMIKOS}")


        rootProject.allprojects
                // 忽略根项目
                .filterNot { it.path == ":" || it.path.startsWith(":yiird-tests") || it.path.endsWith("-dependencies") || it.path.endsWith("-starters") }
                // 将其余项目都加到BOM中
                .forEach { api(project(it.path)) }
    }
}