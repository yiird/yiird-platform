package com.yiird.spring.boot.autoconfigure;

import com.yiird.spring.boot.autoconfigure.common.ConfigurableExcludeAutoConfiguration;
import com.yiird.spring.boot.autoconfigure.common.EnvUtil;
import java.util.ArrayList;
import java.util.List;

public class ExcludeAutoConfiguration extends ConfigurableExcludeAutoConfiguration {


    @Override
    protected List<String> excludes() {
        List<String> exclude = new ArrayList<>();
        if (!EnvUtil.isJta(this.environment)) {
            exclude.add("com.atomikos.spring.AtomikosAutoConfiguration");
        }
        return exclude;
    }
}
