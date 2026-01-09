package web.filter;

import http.request.HttpRequest;
import http.response.HttpResponse;
import web.dispatch.Dispatcher;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {
    private final List<Pair> filterChainList;
    private final Dispatcher dispatcher;

    public FilterChain(Dispatcher dispatcher) {
        this.filterChainList = new ArrayList<>();
        this.dispatcher = dispatcher;
    }

    public FilterChain addChain(String path, List<ServletFilter> filterList) {
        filterChainList.add(Pair.of(path, filterList));
        return this;
    }

    public void runFilterChain(HttpRequest request, HttpResponse response) {
        String requestedPath = request.getPath();

        Pair matched = findMatchedChain(requestedPath);
        List<ServletFilter> filters = (matched == null) ? List.of() : matched.filterList;

        FilterEngine engine = new FilterEngine(dispatcher, request, response, filters);
        engine.doFilter();
    }

    private Pair findMatchedChain(String requestedPath) {
        for (Pair pair : filterChainList) {
            if (isMatched(requestedPath, pair.path)) {
                return pair;
            }
        }
        return null;
    }

    private boolean isMatched(String requestedPath, String filterPath) {
        if (filterPath == null || filterPath.isBlank()) return false;
        if (requestedPath == null) return false;

        if (!requestedPath.startsWith("/")) requestedPath = "/" + requestedPath;
        if (!filterPath.startsWith("/")) filterPath = "/" + filterPath;

        if (!filterPath.contains("*")) {
            return requestedPath.equals(filterPath);
        }

        if (filterPath.endsWith("/**")) {
            String prefix = filterPath.substring(0, filterPath.length() - 3);
            if (prefix.isEmpty()) return true;
            return requestedPath.equals(prefix) || requestedPath.startsWith(prefix + "/");
        }

        if (filterPath.endsWith("/*")) {
            String prefix = filterPath.substring(0, filterPath.length() - 2);
            if (!(requestedPath.equals(prefix) || requestedPath.startsWith(prefix + "/"))) return false;

            String rest = requestedPath.substring(prefix.length());
            if (rest.isEmpty()) return false;
            if (!rest.startsWith("/")) return false;

            String afterSlash = rest.substring(1);
            return !afterSlash.isEmpty() && !afterSlash.contains("/");
        }

        return false;
    }

    private static class Pair {
        private final String path;
        private final List<ServletFilter> filterList;

        private Pair(String path, List<ServletFilter> filterList) {
            this.path = path;
            this.filterList = filterList;
        }

        public static Pair of(String path, List<ServletFilter> filterList) {
            return new Pair(path, filterList);
        }
    }

    public static class FilterEngine {
        private final Dispatcher dispatcher;
        private final HttpRequest request;
        private final HttpResponse response;
        private final List<ServletFilter> filterList;
        private int position = 0;

        public FilterEngine(Dispatcher dispatcher, HttpRequest request, HttpResponse response, List<ServletFilter> filterList) {
            this.dispatcher = dispatcher;
            this.request = request;
            this.response = response;
            this.filterList = filterList;
        }

        public void doFilter() {
            if (position >= filterList.size()) {
                dispatcher.handle(request, response);
                return;
            }
            ServletFilter next = filterList.get(position++);
            next.runFilter(request, response, this);
        }
    }
}
