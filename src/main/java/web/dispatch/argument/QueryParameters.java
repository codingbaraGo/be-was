package web.dispatch.argument;

import java.util.*;

public class QueryParameters {
    private final Map<String, List<String>> params;


    public QueryParameters() {
        this.params = new HashMap<>();
    }

    public void addParams(String key, String value){
        if(!params.containsKey(key)) params.put(key, new ArrayList<>());
        params.get(key).add(value);
    }

    public Optional<String> getValue(String key){
        return Optional.ofNullable(params.get(key).get(0));
    }

    public List<String> getValues(String key){
        if(params.containsKey(key))
            return params.get(key);
        return List.of();
    }
}
