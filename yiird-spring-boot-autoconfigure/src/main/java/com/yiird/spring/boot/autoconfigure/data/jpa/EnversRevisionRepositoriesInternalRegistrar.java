package com.yiird.spring.boot.autoconfigure.data.jpa;

import com.yiird.spring.boot.autoconfigure.data.jpa.config.EnableJpaRepositoriesInternal;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;


@EnableJpaRepositoriesInternal(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class EnversRevisionRepositoriesInternalRegistrar extends JpaRepositoriesInternalRegistrar {

}
