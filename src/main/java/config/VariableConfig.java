package config;

import java.util.List;

public class VariableConfig {
    public static final List<String> STATIC_RESOURCE_ROOTS = List.of(
            "./src/main/resources",
            "./src/main/resources/static");
    public static final List<String> DYNAMIC_RESOURCE_ROOTS = List.of(
            "./src/main/resources/templates"
    );

    public static final long IDLE_MS = 30*60*100;
    public static final long ABSOLUTE_MS = 180*60*100;

    public static final int EMAIL_MAX = 50;
    public static final int EMAIL_MIN = 4;
    public static final int NICKNAME_MAX = 12;
    public static final int NICKNAME_MIN = 4;
    public static final int PASSWORD_MAX = 16;
    public static final int PASSWORD_MIN = 4;
}
