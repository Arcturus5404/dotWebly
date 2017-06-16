package nl.dotWebly.data.service;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
public class InformationProduct {

    private final String name;
    private final String query;
    private final List<String> parameters;

    public String getName() {
        return name;
    }
    public String getQuery() {
        return query;
    }
    public List<String> getParameters() {
        return parameters;
    }

    public InformationProduct(String name, String query, List<String> parameters) {
        this.name = name;
        this.query = query;
        this.parameters = parameters;
    }

}
