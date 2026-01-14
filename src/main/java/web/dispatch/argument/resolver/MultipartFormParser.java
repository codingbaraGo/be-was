package web.dispatch.argument.resolver;

import exception.ErrorCode;
import exception.ServiceException;
import web.dispatch.argument.MultipartFile;
import web.dispatch.argument.MultipartForm;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class MultipartFormParser {

    public MultipartForm parse(byte[] body, String contentTypeHeader) {
        if (contentTypeHeader == null || !contentTypeHeader.toLowerCase().startsWith("multipart/form-data")) {
            throw new ServiceException(ErrorCode.INVALID_INPUT, "Content-Type must be multipart/form-data");
        }

        String boundary = extractBoundary(contentTypeHeader);
        if (boundary == null || boundary.isBlank()) {
            throw new ServiceException(ErrorCode.INVALID_INPUT, "boundary missing in Content-Type");
        }
        if (body == null) body = new byte[0];

        byte[] delimiter = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        byte[] boundaryWithCrlfPrefix = ("\r\n--" + boundary).getBytes(StandardCharsets.ISO_8859_1);

        int pos = 0;
        int first = indexOf(body, delimiter, pos);
        if (first < 0) {
            throw new ServiceException(ErrorCode.INVALID_INPUT, "Invalid multipart body: boundary not found");
        }
        pos = first + delimiter.length;

        if (startsWith(body, pos, "--".getBytes(StandardCharsets.ISO_8859_1))) {
            return MultipartForm.of(Map.of(), Map.of());
        }
        pos = skipCrlf(body, pos);

        Map<String, List<String>> fields = new LinkedHashMap<>();
        Map<String, List<MultipartFile>> files = new LinkedHashMap<>();

        while (pos < body.length) {
            // 1) headers
            Map<String, String> headers = new LinkedHashMap<>();
            while (true) {
                int lineEnd = indexOf(body, "\r\n".getBytes(StandardCharsets.ISO_8859_1), pos);
                if (lineEnd < 0) throw new ServiceException(ErrorCode.INVALID_INPUT, "Invalid multipart headers");

                if (lineEnd == pos) {
                    pos += 2;
                    break;
                }

                String line = new String(body, pos, lineEnd - pos, StandardCharsets.ISO_8859_1);
                int idx = line.indexOf(':');
                if (idx <= 0) throw new ServiceException(ErrorCode.INVALID_INPUT, "Invalid header line: " + line);

                String key = line.substring(0, idx).trim().toLowerCase();
                String value = line.substring(idx + 1).trim();
                headers.put(key, value);

                pos = lineEnd + 2;
            }

            String cd = headers.get("content-disposition");
            if (cd == null) throw new ServiceException(ErrorCode.INVALID_INPUT, "content-disposition missing");

            ContentDisposition parsed = ContentDisposition.parse(cd);
            if (parsed.name == null || parsed.name.isBlank()) {
                throw new ServiceException(ErrorCode.INVALID_INPUT, "part name missing in content-disposition");
            }

            String partContentType = headers.get("content-type"); // may be null

            // 2) content: 다음 boundary(\r\n--boundary) 전까지
            int next = indexOf(body, boundaryWithCrlfPrefix, pos);
            if (next < 0) throw new ServiceException(ErrorCode.INVALID_INPUT, "Invalid multipart: closing boundary not found");

            byte[] partBody = Arrays.copyOfRange(body, pos, next);
            pos = next + 2;

            // 3) boundary line 처리
            int bStart = pos;
            if (!startsWith(body, bStart, delimiter)) {
                throw new ServiceException(ErrorCode.INVALID_INPUT, "Invalid multipart boundary sequence");
            }
            pos = bStart + delimiter.length;

            boolean isFinal = startsWith(body, pos, "--".getBytes(StandardCharsets.ISO_8859_1));
            if (isFinal) {
                pos += 2;
            }
            pos = skipCrlf(body, pos);

            if (parsed.hasFilenameParam) {
                MultipartFile mf = new MultipartFile(parsed.name, partContentType, partBody);
                files.computeIfAbsent(parsed.name, k -> new ArrayList<>()).add(mf);
            } else {
                String value = new String(partBody, StandardCharsets.UTF_8);
                fields.computeIfAbsent(parsed.name, k -> new ArrayList<>()).add(value);
            }

            if (isFinal) break;
        }

        return MultipartForm.of(fields, files);
    }

    private String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String p : parts) {
            String t = p.trim();
            if (t.toLowerCase().startsWith("boundary=")) {
                String b = t.substring("boundary=".length()).trim();
                if (b.startsWith("\"") && b.endsWith("\"") && b.length() >= 2) {
                    b = b.substring(1, b.length() - 1);
                }
                return b;
            }
        }
        return null;
    }

    private int skipCrlf(byte[] body, int pos) {
        if (pos + 2 <= body.length
                && body[pos] == '\r'
                && body[pos + 1] == '\n') {
            return pos + 2;
        }
        return pos;
    }

    private boolean startsWith(byte[] src, int offset, byte[] prefix) {
        if (offset < 0) return false;
        if (offset + prefix.length > src.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (src[offset + i] != prefix[i]) return false;
        }
        return true;
    }

    private int indexOf(byte[] src, byte[] target, int from) {
        if (target.length == 0) return from;
        outer:
        for (int i = Math.max(0, from); i <= src.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (src[i + j] != target[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    private static final class ContentDisposition {
        final String name;
        final boolean hasFilenameParam;

        private ContentDisposition(String name, boolean hasFilenameParam) {
            this.name = name;
            this.hasFilenameParam = hasFilenameParam;
        }

        static ContentDisposition parse(String raw) {
            String[] tokens = raw.split(";");
            String name = null;
            boolean hasFilename = false;

            for (String token : tokens) {
                String t = token.trim();
                int eq = t.indexOf('=');
                if (eq < 0) {
                    continue;
                }
                String k = t.substring(0, eq).trim().toLowerCase();
                String v = t.substring(eq + 1).trim();

                if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                }

                if (k.equals("name")) {
                    name = v;
                } else if (k.equals("filename")) {
                    hasFilename = true;
                }
            }
            return new ContentDisposition(name, hasFilename);
        }
    }
}

