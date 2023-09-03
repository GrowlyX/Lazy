package life.savag3.lazy;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class Config {

    // Packages exempt from being stripped by Lazy (Essentially Skipping & adding to output jar)
    // Format: package0/package1/package2/* - Exempt anything inside this package
    // Format: package0/*/package2 - Exempt anything with the root path `package0` and a sub package `package2`
    @Builder.Default
    public List<String> packagesExemptFromStripping = new ArrayList<>();
    // Packages to be excluded from the output (These packages / classes are not included in the output jar)
    // Format: package0/package1/package2/* - Exclude anything inside this package
    // Format: package0/*/package2 - Exclude anything with the root path `package0` and a sub package `package2`
    @Builder.Default
    public List<String> packagesExcludedFromOutput = new ArrayList<>();

    // Include Public Fields in the output
    @Builder.Default
    public boolean includePublicStaticFields = true;
    // Include Private Fields in the output
    @Builder.Default
    public boolean includePrivateStaticFields = true;
    // Include Public Fields in the output
    @Builder.Default
    public boolean includePublicFields = true;
    // Include Private Fields in the output
    @Builder.Default
    public boolean includePrivateFields = true;
    // Include Private Methods in the output
    @Builder.Default
    public boolean includePrivateMethods = false;
    // Include Native Methods in the output
    @Builder.Default
    public boolean includeNativeMethods = false;
    // Include abstract classes in the output
    @Builder.Default
    @Deprecated
    public boolean includeAbstractClasses = true;
    // Include method content for default interface classes
    @Builder.Default
    @Deprecated
    public boolean includeInterfaceMethods = true;
    // Include enum data in the output
    @Builder.Default
    @Deprecated
    public boolean includeEnumData = true;

    // Do advanced logging
    @Builder.Default
    public boolean doAdvancedLogging = true;
}
