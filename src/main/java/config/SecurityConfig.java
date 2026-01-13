package config;

import exception.ErrorException;

import java.util.List;

public class SecurityConfig extends SingletonContainer {
    private final AppConfig appConfig = new AppConfig();
    private int callCount;

    public void config(){
        if(callCount>0) throw new ErrorException("SecurityConfig::setPaths: Duplicated call");
        setPaths();
        callCount++;
    }

    public void setPaths(){
        appConfig.filterChainContainer()
                .addPath(FilterType.AUTHENTICATED, "/mypage/**")
                .addPaths(FilterType.LOG_IN, List.of("/user/login", "/login"))
                .addPaths(FilterType.PUBLIC, List.of("/", "/home/*"))
                .addPath(FilterType.ALL, "/**");
    }


}
