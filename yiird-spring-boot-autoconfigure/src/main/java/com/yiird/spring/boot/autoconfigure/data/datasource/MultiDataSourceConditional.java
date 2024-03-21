package com.yiird.spring.boot.autoconfigure.data.datasource;

import com.yiird.spring.boot.autoconfigure.common.EnvUtil;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MultiDataSourceConditional extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        ConditionMessage.Builder message = ConditionMessage.forCondition("Multi Data Source");

        boolean isMulti = EnvUtil.isMultiDataSource(context.getEnvironment());

        if (isMulti) {
            return ConditionOutcome.match(message.available("Configuration"));
        } else {
            return ConditionOutcome.noMatch(message.notAvailable("Configuration"));
        }
    }
}
