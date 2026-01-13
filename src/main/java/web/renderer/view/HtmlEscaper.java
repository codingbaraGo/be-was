package web.renderer.view;

public final class HtmlEscaper {
    private HtmlEscaper() {}

    public static String escape(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"","&quot;")
                .replace("'", "&#39;");
    }
}
