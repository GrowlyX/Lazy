package life.savag3.lazy;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Builder
public class Config {

    @NotNull
    private File inputJar;

    @NotNull
    private File outputJar;

    @Nullable
    @Builder.Default
    private Consumer<String> debugLogSink = System.out::println;

    // Packages exempt from being stripped by Lazy (Essentially Skipping & adding to output jar)
    // Format: package0/package1/package2/* - Exempt anything inside this package
    // Format: package0/*/package2 - Exempt anything with the root path `package0` and a sub package `package2`
    @Builder.Default
    private List<String> exemptPackages = new ArrayList<>();
    // Packages to be excluded from the output (These packages / classes are not included in the output jar)
    // Format: package0/package1/package2/* - Exclude anything inside this package
    // Format: package0/*/package2 - Exclude anything with the root path `package0` and a sub package `package2`
    @Builder.Default
    private List<String> excludedPackages = new ArrayList<>();

    public enum ExemptionStrategy {
        WHITELIST, // By default, none are stripped
        BLACKLIST // Only specified packages are not stripped
    }

    @Builder.Default
    private ExemptionStrategy exemptionStrategy = ExemptionStrategy.BLACKLIST;

    public enum ExclusionStrategy {
        WHITELIST, // By default, none are included in the output
        BLACKLIST // Only specified packages are excluded from the output
    }

    @Builder.Default
    private ExclusionStrategy exclusionStrategy = ExclusionStrategy.BLACKLIST;

    // Include Public Fields in the output
    @Builder.Default
    private boolean includePublicStaticFields = true;
    // Include Private Fields in the output
    @Builder.Default
    private boolean includePrivateStaticFields = true;
    // Include Public Fields in the output
    @Builder.Default
    private boolean includePublicFields = true;
    // Include Private Fields in the output
    @Builder.Default
    private boolean includePrivateFields = true;
    // Include Private Methods in the output
    @Builder.Default
    private boolean includePrivateMethods = false;
    // Include Native Methods in the output
    @Builder.Default
    private boolean includeNativeMethods = false;
    // Include abstract classes in the output
    @Builder.Default
    @Deprecated
    private boolean includeAbstractClasses = true;
    // Include method content for default interface classes
    @Builder.Default
    @Deprecated
    private boolean includeInterfaceMethods = true;
    // Include enum data in the output
    @Builder.Default
    @Deprecated
    private boolean includeEnumData = true;

    // Do advanced logging
    @Builder.Default
    private boolean doAdvancedLogging = true;
}
