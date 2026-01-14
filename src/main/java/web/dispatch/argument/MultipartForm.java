package web.dispatch.argument;

import java.util.*;

public class MultipartForm {

    private final Map<String, List<String>> fields;
    private final Map<String, List<MultipartFile>> files;

    private MultipartForm(Map<String, List<String>> fields,
                          Map<String, List<MultipartFile>> files) {
        this.fields = fields;
        this.files = files;
    }

    public static MultipartForm of(Map<String, List<String>> fields,
                                   Map<String, List<MultipartFile>> files) {
        return new MultipartForm(fields == null ? new HashMap<>() : fields,
                files == null ? new HashMap<>() : files);
    }

    public Map<String, List<String>> getFields() {
        return fields;
    }

    public Map<String, List<MultipartFile>> getFiles() {
        return files;
    }

    public Optional<String> getField(String name) {
        List<String> vs = fields.get(name);
        if (vs == null || vs.isEmpty()) return Optional.empty();
        return Optional.ofNullable(vs.get(0));
    }

    public List<String> getFieldValues(String name) {
        return fields.getOrDefault(name, List.of());
    }

    public Optional<MultipartFile> getFile(String name) {
        List<MultipartFile> fs = files.get(name);
        if (fs == null || fs.isEmpty()) return Optional.empty();
        return Optional.ofNullable(fs.get(0));
    }

    public List<MultipartFile> getFiles(String name) {
        return files.getOrDefault(name, List.of());
    }
}
