package life.savag3.lazy;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Lazy {

    @NotNull
    private final JarFile jar;

    private final HashMap<String, byte[]> results = new HashMap<>();

    @Getter
    @Nullable
    private Instant start;

    @Getter
    @NotNull
    private final Config config;

    @NotNull
    private final PackageUtils packageUtils;

    public Long getElapsedTime() {
        if (start == null) {
            return 0L;
        }

        return System.currentTimeMillis() - this.start.toEpochMilli();
    }

    protected void debugLog(@NotNull String message) {
        if (this.config.getDebugLogSink() == null) {
            return;
        }

        this.config.getDebugLogSink().accept(message);
    }

    @NotNull
    public static Lazy create(@NotNull File input, @NotNull File output) {
        if (input.exists()) {
            throw new IllegalStateException(
                "Input jar file does not exist!"
            );
        }

        return new Lazy(
            Config
                .builder()
                .inputJar(input)
                .outputJar(output)
                .build()
        );
    }

    @NotNull
    public static Lazy create(@NotNull Config config) {
        return new Lazy(config);
    }

    protected Lazy(@NotNull Config config) {
        this.config = config;
        this.packageUtils = new PackageUtils(this.config);

        // Print working paths for debugging and testing
        debugLog("Working Dir - " + new File("").getAbsolutePath());
        debugLog(" ");
        debugLog("Reading Jar... (" + this.config.getInputJar().getAbsolutePath() + ")");

        try {
            this.jar = new JarFile(this.config.getInputJar());
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to read jar file " + this.config.getInputJar().getAbsolutePath()
            );
        }

        try {
            this.config.getOutputJar().createNewFile();
        } catch (IOException er) {
            throw new IllegalStateException(
                "Couldn't create output file... exiting"
            );
        }
    }

    public void start() {
        this.start = Instant.now();

        // Enumerate over jarfile entries
        for (Enumeration<JarEntry> list = jar.entries(); list.hasMoreElements(); ) {
            JarEntry clazz = list.nextElement();
            if (clazz.isDirectory()) continue;
            // We only care about class files.
            if (!clazz.getName().endsWith(".class")) continue;
            try {
                // Check if a class is excluded | true ? skip : process
                if (this.packageUtils.isExcluded(clazz.getName())) continue;
                // Check if a class is exempt | true ? write whole class to output : write stripped class to output
                debugLog("Processing " + clazz.getName());
                if (this.packageUtils.isExempt(clazz.getName())) {
                    add(clazz.getName(), jar.getInputStream(clazz).readAllBytes());
                } else {
                    LazyClassTransformer transformer = new LazyClassTransformer(jar.getInputStream(clazz).readAllBytes(), this.config);
                    add(clazz.getName(), transformer.transform());
                }
            } catch (Exception e) {
                debugLog("Failed to read class: " + clazz.getName() + " - " + e.getMessage());
                debugLog("Skipping class...");
            }
        }

        // Pack & write the output jar
        pack();
    }

    /**
     * Add byte that represent a class to the results map to be written to final jar.
     * @param pack The package & class name `life/savag3/example/Core.class`
     * @param bytes The bytes that are contained in the cleaned class.
     */
    public void add(String pack, byte[] bytes) {
        this.results.put(pack, bytes);
    }

    /**
     * Pack the results of the classes stored in results map into a new jar file.
     */
    @SneakyThrows
    public void pack() {
        String outputJarPath = this.getConfig().getOutputJar().getAbsolutePath();
        debugLog(" ");
        debugLog(" Writing new Jar (" + outputJarPath + ")");

        JarOutputStream jarOutputStream = new JarOutputStream(
            new BufferedOutputStream(
                new FileOutputStream(this.getConfig().getOutputJar().getAbsolutePath())
            ),
            this.jar.getManifest()
        );

        for (Map.Entry<String, byte[]> pack : this.results.entrySet()) {
            if (this.getConfig().isDoAdvancedLogging())  debugLog(" .. Writing " + pack.getKey());
            JarEntry j = new JarEntry(pack.getKey());
            j.setSize(pack.getValue().length);
            jarOutputStream.putNextEntry(j);
            jarOutputStream.write(pack.getValue());
            jarOutputStream.closeEntry();
            if (this.getConfig().isDoAdvancedLogging())  debugLog(" ... Done\n");
        }
        jarOutputStream.flush();
        jarOutputStream.close();
        jar.close();

        debugLog(" ");
        debugLog("Jar saved to " + outputJarPath + " in " + Duration.between(start, Instant.now()).toMillis() + "ms");
        debugLog("Original Size: " + this.getConfig().getInputJar().length() + " bytes, New size: " + this.getConfig().getInputJar().length() + " bytes; Size reduced by " + (Math.abs((1.0f - ((float) this.getConfig().getOutputJar().length() / (float) this.getConfig().getInputJar().length()))) * 100.0f) + "%");
    }
}
