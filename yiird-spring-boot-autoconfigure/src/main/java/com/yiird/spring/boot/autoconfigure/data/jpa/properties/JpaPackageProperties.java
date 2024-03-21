package com.yiird.spring.boot.autoconfigure.data.jpa.properties;

import com.yiird.spring.boot.autoconfigure.common.EnvUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "spring.jpa", ignoreInvalidFields = true)
public class JpaPackageProperties {

    private List<ModulePackage> packages;

    @Getter
    @Setter
    public static class ModulePackage {

        private String dataSourceKey;
        private String[] modulePackages;
        private Set<String> modelScanPackages = new HashSet<>();

        private Set<String> repositoryScanPackages = new HashSet<>();

        public Set<String> getModelScanPackages() {
            if (Objects.nonNull(modulePackages) && modulePackages.length > 0) {
                modelScanPackages.addAll(EnvUtil.packageChain(modulePackages, "model"));
            }
            return modelScanPackages;
        }

        public Set<String> getRepositoryScanPackages() {
            if (Objects.nonNull(modulePackages) && modulePackages.length > 0) {
                repositoryScanPackages.addAll(EnvUtil.packageChain(modulePackages, "repo", "repository"));
            }
            return repositoryScanPackages;
        }
    }

    public Map<String, ModulePackage> map() {
        Map<String, ModulePackage> result = new HashMap<>();
        this.packages.forEach((it) -> {
            result.put(it.dataSourceKey, it);
        });
        return result;
    }
}
