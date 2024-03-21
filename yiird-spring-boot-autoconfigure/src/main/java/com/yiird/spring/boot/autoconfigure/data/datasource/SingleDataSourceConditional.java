package com.yiird.spring.boot.autoconfigure.data.datasource;

import com.yiird.spring.boot.autoconfigure.common.EnvUtil;
import com.yiird.spring.boot.autoconfigure.common.PropertiesKeyConstants;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class SingleDataSourceConditional extends SpringBootCondition {


    @Override
    public ConditionOutcome getMatchOutcome(
        ConditionContext context,
        AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Single Data Source");
        Environment environment = context.getEnvironment();

        String property = environment.getProperty(PropertiesKeyConstants.DATASOURCE_URL_PROPERTY);
        boolean isSingle = EnvUtil.isSingleDataSource(environment);

        if (StringUtils.hasText(property) && isSingle) {
            return ConditionOutcome.match(message.available("Configuration"));
        } else {
            return ConditionOutcome.noMatch(message.notAvailable("Configuration"));
        }
    }
}