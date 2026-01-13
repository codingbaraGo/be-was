package web.renderer.view;

import config.VariableConfig;
import exception.ErrorException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class TemplateLoader {
    private final List<String> roots = VariableConfig.DYNAMIC_RESOURCE_ROOTS;

    public String load(String viewPath) {
        Path file = resolve(viewPath)
                .orElseThrow(() -> new ErrorException("Template not found: " + viewPath));

        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ErrorException("Template read error: " + viewPath, e);
        }
    }

    private Optional<Path> resolve(String viewPath) {
        String relative = normalize(viewPath);
        for (String root : roots) {
            Path p = Paths.get(root).resolve(relative).normalize();
            if (Files.exists(p) && Files.isRegularFile(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    private String normalize(String viewPath) {
        if (viewPath == null || viewPath.isBlank()) return "";
        return viewPath.startsWith("/") ? viewPath.substring(1) : viewPath;
    }
}
