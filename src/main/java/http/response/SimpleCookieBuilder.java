package http.response;

public class SimpleCookieBuilder implements ResponseCookie{
    private final String cookieString;
    
    private SimpleCookieBuilder(String str){
        this.cookieString = str;
    }
    
    public static SimpleCookieBuilder of(String str){
        return new SimpleCookieBuilder(str);
    }
    
    @Override
    public String toHeaderValue() {
        return cookieString;
    }
}
