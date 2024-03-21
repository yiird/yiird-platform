package com.yiird.spring.boot.autoconfigure.data.jpa;


import com.yiird.spring.boot.autoconfigure.data.datasource.MultiDataSourceConditional;
import com.yiird.spring.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.JpaRepositoriesImportSelector;
import com.yiird.spring.boot.autoconfigure.data.jpa.properties.JpaPackageProperties;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.util.ClassUtils;

@AutoConfiguration(
    after = {HibernateJpaAutoConfiguration.class, TaskExecutionAutoConfiguration.class},
    before = org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
)
@ConditionalOnBean({DataSource.class})
@ConditionalOnClass({JpaRepository.class})
@ConditionalOnMissingBean({JpaRepositoryFactoryBean.class, JpaRepositoryConfigExtension.class})
@ConditionalOnProperty(
    prefix = "spring.data.jpa.repositories",
    name = {"enabled"},
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(JpaPackageProperties.class)
@Conditional(MultiDataSourceConditional.class)
@Import(JpaRepositoriesImportSelector.class)
class JpaRepositoriesAutoConfiguration {

    @Bean
    @Conditional({BootstrapExecutorCondition.class})
    public EntityManagerFactoryBuilderCustomizer entityManagerFactoryBootstrapExecutorCustomizer(Map<String, AsyncTaskExecutor> taskExecutors) {
        return (builder) -> {
            AsyncTaskExecutor bootstrapExecutor = this.determineBootstrapExecutor(taskExecutors);
            builder.setBootstrapExecutor(bootstrapExecutor);
        };
    }

    private AsyncTaskExecutor determineBootstrapExecutor(Map<String, AsyncTaskExecutor> taskExecutors) {
        return taskExecutors.size() == 1 ? (AsyncTaskExecutor) taskExecutors.values().iterator().next() : (AsyncTaskExecutor) taskExecutors.get("applicationTaskExecutor");
    }


    private static final class BootstrapExecutorCondition extends AnyNestedCondition {

        BootstrapExecutorCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(
            prefix = "spring.data.jpa.repositories",
            name = {"bootstrap-mode"},
            havingValue = "lazy"
        )
        static class LazyBootstrapMode {

        }

        @ConditionalOnProperty(
            prefix = "spring.data.jpa.repositories",
            name = {"bootstrap-mode"},
            havingValue = "deferred"
        )
        static class DeferredBootstrapMode {

        }
    }


    static class JpaRepositoriesImportSelector implements DeferredImportSelector {

        private static final boolean ENVERS_AVAILABLE = ClassUtils.isPresent(
            "org.springframework.data.envers.repository.config.EnableEnversRepositories",
            JpaRepositoriesImportSelector.class.getClassLoader());

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{determineImport()};
        }

        private String determineImport() {
            return ENVERS_AVAILABLE ? EnversRevisionRepositoriesInternalRegistrar.class.getName()
                : JpaRepositoriesInternalRegistrar.class.getName();
        }

    }
}
