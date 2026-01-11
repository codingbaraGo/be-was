package config;

import exception.ErrorException;
import web.filter.ServletFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterConfig extends SingletonContainer {
    private final AppConfig appConfig = new AppConfig();
    private int callCount = 0;

    public void config(){
        if(callCount>0) throw new ErrorException("FilterConfig::set: Duplicated call");
        setFilterChains();
        callCount++;
    }

    private void setFilterChains(){
        appConfig.filterChainContainer()
                .addFilterList(FilterType.ALL, getFilterListByAuthorityType(FilterType.ALL))
                .addFilterList(FilterType.PUBLIC, getFilterListByAuthorityType(FilterType.PUBLIC))
                .addFilterList(FilterType.AUTHENTICATED, getFilterListByAuthorityType(FilterType.AUTHENTICATED))
                .addFilterList(FilterType.RESTRICT, getFilterListByAuthorityType(FilterType.RESTRICT));
    }

    private List<ServletFilter> commonFrontFilter(){
        return getOrCreate("commonFrontFilter",
                () -> List.of(
                        appConfig.accessLogFilter()
                ));
    }

    private List<ServletFilter> commonBackFilter(){
        return getOrCreate("commonBackFilter",
                () -> List.of());
    }

    private List<ServletFilter> getFilterListByAuthorityType(FilterType type) {
        List<ServletFilter> servletFilterList = new ArrayList<>();
        servletFilterList.addAll(commonFrontFilter());
        servletFilterList.addAll(authorizedFilterList(type));
        servletFilterList.addAll(commonBackFilter());
        return servletFilterList;
    }

    private List<ServletFilter> authorizedFilterList(FilterType type) {
        return switch (type) {
            case ALL -> List.of();
            case PUBLIC -> List.of();
            case AUTHENTICATED -> List.of();
            case RESTRICT -> List.of(appConfig.restrictedFilter());
            case LOG_IN -> List.of();
        };
    }
}
