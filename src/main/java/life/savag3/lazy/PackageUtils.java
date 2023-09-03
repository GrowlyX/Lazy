package life.savag3.lazy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PackageUtils {

    @NotNull
    private final Config config;

    public PackageUtils(@NotNull Config config) {
        this.config = config;
    }

    public boolean isExempt(String package0) {
        if (config.getExemptPackages().isEmpty()) {
            return config.getExemptionStrategy() == Config.ExemptionStrategy.BLACKLIST;
        }

        boolean matchResult = matchPatterns(
            config.getExemptPackages(), package0
        );

        return (config.getExemptionStrategy() == Config.ExemptionStrategy.BLACKLIST) == matchResult;
    }

    public boolean isExcluded(String package0) {
        if (config.getExcludedPackages().isEmpty()) {
            return config.getExclusionStrategy() == Config.ExclusionStrategy.BLACKLIST;
        }

        boolean matchResult = matchPatterns(
            config.getExcludedPackages(), package0
        );

        return (config.getExclusionStrategy() == Config.ExclusionStrategy.BLACKLIST) == matchResult;
    }

    private boolean matchPatterns(List<String> targets, String package0) {
        top: for (String excluded : targets) {
            if (excluded.equals(package0)) return true;
            if (excluded.contains("*")) {
                if (excluded.endsWith("*")) {
                    if (package0.startsWith(excluded.substring(0, excluded.length() - 1))) return true;
                } else {
                    final String[] parts = excluded.split("\\*");
                    int currentIdx = 0;
                    for (String part : parts) {
                        int idx = package0.indexOf(part, currentIdx);
                        if (idx == -1) continue top;
                        currentIdx = idx + part.length();
                    }
                }
            }
        }
        return false;
    }
}
