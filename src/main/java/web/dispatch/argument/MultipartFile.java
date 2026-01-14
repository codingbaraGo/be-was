package web.dispatch.argument;

import exception.ErrorException;

import java.util.Objects;

public record MultipartFile(String fieldName, String contentType, byte[] bytes) {

    public MultipartFile(String fieldName, String contentType, byte[] bytes) {
        if (fieldName == null || fieldName.isBlank()) {
            throw new ErrorException("MultipartFile: fieldName required");
        }
        this.fieldName = fieldName;
        this.contentType = (contentType == null || contentType.isBlank()) ? null : contentType;
        this.bytes = Objects.requireNonNullElseGet(bytes, () -> new byte[0]);
    }

    public int size() {
        return bytes.length;
    }
}
