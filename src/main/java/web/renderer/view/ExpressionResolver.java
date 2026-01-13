package web.renderer.view;

import java.lang.reflect.Method;
import java.util.Map;

public class ExpressionResolver {

    public Object resolve(String expr, Map<String, Object> model) {
        if (expr == null || expr.isBlank()) return null;

        String[] parts = expr.trim().split("\\.");
        Object cur = model.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (cur == null) return null;
            cur = getProperty(cur, parts[i]);
        }
        return cur;
    }

    private Object getProperty(Object target, String name) {
        if (target instanceof Map<?, ?> m) {
            return m.get(name);
        }

        Class<?> c = target.getClass();

        // getXxx()
        String getter = "get" + capitalize(name);
        try {
            Method method = c.getMethod(getter);
            return method.invoke(target);
        } catch (Exception ignore) {}

        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
