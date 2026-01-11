package config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class SingletonContainer {
    private static final Map<String, Object> singletonMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(String name, Supplier<T> factory) {
        Object instance = singletonMap.get(name);
        if (instance != null) {
            return (T) instance;
        }
        T created = factory.get();
        singletonMap.put(name, created);
        return created;
    }

}
