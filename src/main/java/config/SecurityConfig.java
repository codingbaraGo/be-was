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
        if(callCount>0) throw new ErrorException("SecurityConfig::setPaths: Duplicated call");
        appConfig.filterChainContainer()
                .addPath(FilterType.AUTHENTICATED, "/mypage/**")
                .addPath(FilterType.ALL, "/user/**")
                .addPath(FilterType.PUBLIC, "/**");
        callCount++;
    }


}
