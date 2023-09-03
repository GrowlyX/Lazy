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
        if (config.getPackagesExemptFromStripping().isEmpty()) return false;
        return matchPatterns(config.getPackagesExemptFromStripping(), package0);
    }

    public boolean isExcluded(String package0) {
        if (config.getPackagesExcludedFromOutput().isEmpty()) return false;
        return matchPatterns(config.getPackagesExcludedFromOutput(), package0);
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
