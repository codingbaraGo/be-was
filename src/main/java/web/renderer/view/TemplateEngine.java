package web.renderer.view;

import exception.ErrorException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {
    private final TemplateLoader loader;
    private final ExpressionResolver resolver;

    private final ConcurrentHashMap<String, String> templateCache = new ConcurrentHashMap<>();

    private static final Pattern INCLUDE = Pattern.compile("\\{\\{>\\s*([^}]+?)\\s*\\}\\}");

    // 블록 오프닝 태그 패턴
    private static final Pattern IF_OPEN = Pattern.compile("\\{\\{#if(\\d+)\\s+([^}]+)\\}\\}");
    private static final Pattern EACH_OPEN = Pattern.compile("\\{\\{#each(\\d+)\\s+([^}]+)\\}\\}");

    // 변수 치환 패턴
    private static final Pattern RAW_VAR = Pattern.compile("\\{\\{\\{\\s*([^}]+?)\\s*\\}\\}\\}");
    private static final Pattern VAR = Pattern.compile("\\{\\{\\s*([^}]+?)\\s*\\}\\}");

    public TemplateEngine(TemplateLoader loader, ExpressionResolver resolver) {
        this.loader = loader;
        this.resolver = resolver;
    }

    public String render(String viewPath, Map<String, Object> model) {
        String template = templateCache.computeIfAbsent(viewPath, loader::load);
        return renderText(template, model == null ? Map.of() : model);
    }

    public void clearCache() {
        templateCache.clear();
    }

    private String processIncludes(String s, Map<String, Object> model) {
        while (true) {
            Matcher m = INCLUDE.matcher(s);
            if (!m.find()) break;

            StringBuffer sb = new StringBuffer();
            do {
                String includePath = m.group(1).trim();

                // include는 "렌더 완료된 결과"를 삽입해야 번호(if1 등) 충돌이 없음
                String includedHtml = render(includePath, model);

                m.appendReplacement(sb, Matcher.quoteReplacement(includedHtml));
            } while (m.find());

            m.appendTail(sb);
            s = sb.toString();
        }
        return s;
    }

    private String renderText(String text, Map<String, Object> model) {
        if (text == null) return "";

        String s = processIncludes(text, model);

        // 1) 블록 처리(더 이상 없을 때까지 반복)
        while (true) {
            int nextIf = indexOfRegex(s, IF_OPEN);
            int nextEach = indexOfRegex(s, EACH_OPEN);

            if (nextIf < 0 && nextEach < 0) break;

            if (nextIf >= 0 && (nextEach < 0 || nextIf < nextEach)) {
                s = processIfBlockAt(s, nextIf, model);
            } else {
                s = processEachBlockAt(s, nextEach, model);
            }
        }

        // 2) 변수 치환(Triple braces 먼저)
        s = replaceRawVars(s, model);
        s = replaceVars(s, model);

        return s;
    }

    private String processIfBlockAt(String s, int start, Map<String, Object> model) {
        Matcher m = IF_OPEN.matcher(s);
        if (!m.find(start) || m.start() != start) {
            throw new ErrorException("Template parse error: invalid if open tag");
        }

        String num = m.group(1);
        String condKey = m.group(2).trim();

        int openEnd = m.end();
        String closeTag = "{{/if" + num + "}}";
        int closeIdx = s.indexOf(closeTag, openEnd);
        if (closeIdx < 0) {
            throw new ErrorException("Template parse error: missing " + closeTag);
        }

        String body = s.substring(openEnd, closeIdx);

        String elseTag = "{{else" + num + "}}";
        int elsePos = body.indexOf(elseTag);

        String thenPart = (elsePos >= 0) ? body.substring(0, elsePos) : body;
        String elsePart = (elsePos >= 0) ? body.substring(elsePos + elseTag.length()) : "";

        boolean cond = truthy(resolver.resolve(condKey, model));
        String chosen = cond ? thenPart : elsePart;

        String rendered = renderText(chosen, model);

        return s.substring(0, start) + rendered + s.substring(closeIdx + closeTag.length());
    }

    private String processEachBlockAt(String s, int start, Map<String, Object> model) {
        Matcher m = EACH_OPEN.matcher(s);
        if (!m.find(start) || m.start() != start) {
            throw new ErrorException("Template parse error: invalid each open tag");
        }

        String num = m.group(1);
        String listKey = m.group(2).trim();

        int openEnd = m.end();
        String closeTag = "{{/each" + num + "}}";
        int closeIdx = s.indexOf(closeTag, openEnd);
        if (closeIdx < 0) {
            throw new ErrorException("Template parse error: missing " + closeTag);
        }

        String body = s.substring(openEnd, closeIdx);

        Object v = resolver.resolve(listKey, model);
        Iterable<?> items = toIterable(v);

        StringBuilder out = new StringBuilder();
        int i = 0;

        if (items != null) {
            for (Object item : items) {
                Map<String, Object> overlay = new HashMap<>(model);

                // 핵심: 현재 아이템을 this로 제공 (DTO든 Map이든 상관 없음)
                overlay.put("this", item);
                overlay.put("index", i);

                // (선택) item이 Map이면 기존처럼 바로 {{key}} 접근도 되게 유지할 수 있음
                if (item instanceof Map<?, ?> mapItem) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> itemMap = (Map<String, Object>) mapItem;
                    overlay.putAll(itemMap); // 키 충돌 가능성은 있음
                }

                out.append(renderText(body, overlay));
                i++;
            }
        }

        return s.substring(0, start) + out + s.substring(closeIdx + closeTag.length());
    }

    private String replaceRawVars(String s, Map<String, Object> model) {
        Matcher m = RAW_VAR.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1).trim();
            Object v = resolver.resolve(key, model);
            String repl = (v == null) ? "" : v.toString(); // raw
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String replaceVars(String s, Map<String, Object> model) {
        Matcher m = VAR.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String expr = m.group(1).trim();

            Object v = resolver.resolve(expr, model);
            String repl = (v == null) ? "" : HtmlEscaper.escape(v.toString());
            m.appendReplacement(sb, Matcher.quoteReplacement(repl));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private boolean truthy(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        if (v instanceof Number n) return n.doubleValue() != 0.0;
        if (v instanceof CharSequence cs) return cs.length() > 0;
        if (v instanceof Collection<?> c) return !c.isEmpty();
        if (v instanceof Map<?, ?> m) return !m.isEmpty();
        return true;
    }

    private Iterable<?> toIterable(Object v) {
        if (v == null) return null;
        if (v instanceof Iterable<?> it) return it;
        return null; // 단순화: 배열/Map 순회는 이번 버전에서 제외
    }

    private int indexOfRegex(String s, Pattern p) {
        Matcher m = p.matcher(s);
        return m.find() ? m.start() : -1;
    }
}
