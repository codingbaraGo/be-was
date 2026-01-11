package web.filter;

import config.FilterType;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.request.HttpRequest;
import http.response.HttpResponse;
import web.dispatch.Dispatcher;

import java.util.*;

public class FilterChainContainer {
    private final List<FilterTypePaths> registeredPaths;
    private final Map<FilterType, List<ServletFilter>> filterChainMap;
    private final Dispatcher dispatcher;

    public FilterChainContainer(Dispatcher dispatcher) {
        this.registeredPaths = new ArrayList<>();
        this.filterChainMap = new HashMap<>();
        this.dispatcher = dispatcher;
    }

    public void runFilterChain(HttpRequest request, HttpResponse response) {
        String requestedPath = request.getPath();

        FilterTypePaths matched = findMatchedChain(requestedPath).orElseThrow(()-> new ServiceException(ErrorCode.FORBIDDEN));
        List<ServletFilter> filters = filterChainMap.get(matched.type);

        FilterChainEngine engine = new FilterChainEngine(dispatcher, request, response, filters);
        engine.doFilter();
    }

    public FilterChainContainer addFilterList(FilterType type, List<ServletFilter> filterList) {
        if(filterChainMap.containsKey(type))
            throw new ErrorException("FilterChain Construction: Duplicate filter list per type");
        filterChainMap.put(type, filterList);
        return this;
    }

    public FilterChainContainer addPaths(FilterType type, List<String> paths) {
        registeredPaths.add(FilterTypePaths.of(type, paths));
        return this;
    }

    public FilterChainContainer addPath(FilterType type, String path) {
        registeredPaths.add(FilterTypePaths.of(type, List.of(path)));
        return this;
    }

    private Optional<FilterTypePaths> findMatchedChain(String requestedPath) {
        for (FilterTypePaths filterTypePaths : registeredPaths) {
            for (String path : filterTypePaths.paths) {
                if (isMatched(requestedPath, path)) {
                    return Optional.of(filterTypePaths);
                }
            }
        }
        return Optional.empty();
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

    private static class FilterTypePaths {
        private final FilterType type;
        private final List<String> paths;

        public FilterTypePaths(FilterType type, List<String> paths) {
            this.type = type;
            this.paths = paths;
        }

        public static FilterTypePaths of(FilterType type, List<String> paths) {
            return new FilterTypePaths(type, paths);
        }
    }

    public static class FilterChainEngine {
        private final Dispatcher dispatcher;
        private final HttpRequest request;
        private final HttpResponse response;
        private final List<ServletFilter> filterList;
        private int position = 0;

        public FilterChainEngine(Dispatcher dispatcher, HttpRequest request, HttpResponse response, List<ServletFilter> filterList) {
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
