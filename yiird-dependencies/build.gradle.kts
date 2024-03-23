javaPlatform {
    allowDependencies()
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
                .forEach {
                    api(project(it.path))
                }
    }
}