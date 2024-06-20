javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("com.alibaba:druid:${Versions.DRUID}")
        api("com.mysql:mysql-connector-j:${Versions.MYSQL}")
        api("com.atomikos:transactions-spring-boot3-starter:${Versions.ATOMIKOS}")
        api("commons-net:commons-net:${Versions.APACHE_COMMONS_NET}")
        api("org.apache.commons:commons-pool2:${Versions.APACHE_COMMONS_POOL}")
        api("org.apache.tika:tika-core:${Versions.APACHE_TIKA}")
        api("org.apache.tika:tika-parsers-standard-package:${Versions.APACHE_TIKA}")



        rootProject.allprojects
                // 忽略根项目
                .filterNot { it.path == ":" || it.path.startsWith(":yiird-tests") || it.path.endsWith("-dependencies") || it.path.endsWith("-starters") }
                // 将其余项目都加到BOM中
                .forEach {
                    api(project(it.path))
                }
    }
}