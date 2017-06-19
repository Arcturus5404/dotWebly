package nl.dotWebly.data.service.impl;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static nl.dotWebly.data.service.QueryParser.getParametersFromQuery;
import static nl.dotWebly.data.utils.QueryUtils.expand;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class InformationProductServiceImpl implements InformationProductService {

    private final TripleStoreClientImpl<ConfigurationRepository> client;
//    private final List<TripleStoreClient> clients;

    public static final String ELMO_INFORMATIONPRODUCT = "elmo:InformationProduct";
    public static final String ELMO_QUERY = "elmo:query";

    public static final String SELECT_ALL_QUERY = "SELECT ?s ?p ?o WHERE { ?s rdf:type %1$s. ?s ?p ?o }";
    public static final String SELECT_ONE_QUERY = "SELECT ?s ?p ?o WHERE { <%1$s> rdf:type %2$s. ?s ?p ?o }";

    @Autowired
    public InformationProductServiceImpl(TripleStoreClientImpl<ConfigurationRepository> client) {
        this.client = client;
//        this.clients = clients;
    }

    /**
     * Get all the information products
     *
     * @return information products names
     */
    @Override
    public List<InformationProduct> getInformationProducts() {
        String query = String.format(SELECT_ALL_QUERY, ELMO_INFORMATIONPRODUCT);
        List<Map<String, Value>> result = client.select(query);

        return convertToInformationProducts(result);
    }

    /**
     * Get specific information products
     *
     * @return information products names
     */
    @Override
    public Optional<InformationProduct> getInformationProduct(String name) {
        String query = String.format(SELECT_ONE_QUERY, expand(name), ELMO_INFORMATIONPRODUCT);
        List<Map<String, Value>> result = client.select(query);

        List<InformationProduct> informationProducts = convertToInformationProducts(result);

        //Result should never exceed 1
        assert informationProducts.size() <= 1;

        return informationProducts.stream().findFirst();
    }

    /**
     * Get specific information products
     *
     * @return information products names
     */
    @Override
    public Model queryInformationProduct(String name, Map<String, String> parameters) {
        InformationProduct product = getInformationProduct(name).orElseThrow(() -> new IllegalStateException("InformationProduct not found"));
        String query = buildQuery(product.getQuery(), parameters);

        //is every parameter filled, does it still contain
        if(query.contains("@")) {
            throw new IllegalStateException("Not every parameter is filled: " + query);
        }

        return client.construct(query);
    }

    private String buildQuery(String template, Map<String, String> parameters) {
        if(parameters == null) {
            return template;
        }

        String query = template;
        for (String key : parameters.keySet()) {
            query = query.replace("@" + key + "@", parameters.get(key));
        }
        return query;
    }

    private List<InformationProduct> convertToInformationProducts(List<Map<String, Value>> queryResult) {
        Map<String, InformationProduct> result = new HashMap<>();

        filterBy(queryResult, "o", ELMO_INFORMATIONPRODUCT, i -> {
            String name = i.get("s").stringValue();
            result.put(name, new InformationProduct(name));
        });

        filterBy(queryResult, "p", ELMO_QUERY, i -> {
            String name = i.get("s").stringValue();
            String query = i.get("o").stringValue();

            //Name should be present, because of the query, if its not, the query has been tampered with
            assert result.containsKey(name);

            InformationProduct informationProduct = result.get(name);
            informationProduct.setQuery(query);

            List<String> parameters = getParametersFromQuery(query);
            informationProduct.setParameters(parameters);
        });

        return result.values().stream().collect(toList());
    }

    private void filterBy(List<Map<String, Value>> queryResult, String parameter, String shouldEquals, Consumer<Map<String, Value>> doAction) {
        queryResult.stream().filter(q -> q.get(parameter).stringValue().equals(expand(shouldEquals))).forEach(doAction);
    }
}
