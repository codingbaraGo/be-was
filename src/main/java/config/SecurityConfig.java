package config;

import exception.ErrorException;

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
                .addPath(FilterType.LOG_IN, "/user/login")
                .addPath(FilterType.ALL, "/user/**")
                .addPath(FilterType.PUBLIC, "/**");
    }


}
