package nl.dotWebly.data.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static nl.dotWebly.data.service.QueryParser.getParametersFromQuery;
import static nl.dotWebly.data.utils.QueryUtils.expand;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class InformationProductService {
    private final TripleStoreClientImpl<ConfigurationRepository> client;

    public static final String ELMO_INFORMATIONPRODUCT = "elmo:InformationProduct";
    public static final String ELMO_QUERY = "elmo:query";

    public static final String SELECT_QUERY = "SELECT ?s ?p ?o WHERE { ?s rdf:type %1$s. ?s ?p ?o }";

    @Autowired
    public InformationProductService(TripleStoreClientImpl<ConfigurationRepository> client) {
        this.client = client;
    }

    /**
     * Get all the information products
     *
     * @return information products names
     */
    public List<InformationProduct> getInformationProducts() {
        String query = String.format(SELECT_QUERY, ELMO_INFORMATIONPRODUCT);
        List<Map<String, Value>> result = client.select(query);

        return convertToInformationProducts(result);
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
            if(!result.containsKey(name)) {
                throw new IllegalStateException("Could not get information products correctly, please check your query!");
            }

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
//
//    private InformationProduct createInformationProduct(BindingSet set) {
//        String name = model.subjects().stream().findFirst().get().stringValue();
//        String query = model.filter(null, valueFactory.createIRI(ELMO_QUERY), null).objects().stream().findFirst().get().stringValue();
//
//        List<String> parameters = getParametersFromQuery(query);
//
//        return new InformationProduct(name, query, parameters);
//    }
}
