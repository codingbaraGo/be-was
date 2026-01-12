package config;

import java.util.List;

public class VariableConfig {
    public static final List<String> STATIC_RESOURCE_ROOTS = List.of(
            "./src/main/resources",
            "./src/main/resources/static");

    public static final long IDLE_MS = 30*60*100;
    public static final long ABSOLUTE_MS = 180*60*100;
}
