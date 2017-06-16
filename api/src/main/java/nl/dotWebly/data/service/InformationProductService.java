package nl.dotWebly.data.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.utils.ModelUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static nl.dotWebly.data.service.QueryParser.getParametersFromQuery;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class InformationProductService {
    private final TripleStoreClientImpl<ConfigurationRepository> client;

    public static final String ELMO_INFORMATIONPRODUCT = "http://bp4mc2.org/elmo/def#InformationProduct";
    public static final String ELMO_QUERY = "http://bp4mc2.org/elmo/def#query";
    public static final String CONSTRUCT_QUERY = "CONSTRUCT { ?s ?p <%1$s>. ?s ?p2 ?o } WHERE { ?s ?p <%1$s>. ?s ?p2 ?o }";

    @Autowired
    public InformationProductService(TripleStoreClientImpl<ConfigurationRepository> client) {
        this.client = client;
    }

    /**
     * Get all the information products
     *
     * @return information products names
     */
    public InformationProduct[] getInformationProducts() {
        String query = String.format(CONSTRUCT_QUERY, ELMO_INFORMATIONPRODUCT);
        Model model = client.construct(query);

        List<Model> products = ModelUtils.filterBySubject(model);

        return products.stream().map(this::createInformationProduct).toArray(InformationProduct[]::new);
    }

    private InformationProduct createInformationProduct(Model model) {
        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        String name = model.subjects().stream().findFirst().get().stringValue();
        String query = model.filter(null, valueFactory.createIRI(ELMO_QUERY), null).objects().stream().findFirst().get().stringValue();

        List<String> parameters = getParametersFromQuery(query);

        return new InformationProduct(name, query, parameters);
    }


    /**
     * Get the meta data for the information product
     *
     * @param informationProduct
     * @return
     */
    public String[] getMetadata(String informationProduct) {
        //get all information products
        Model model = client.queryBy(null, null, ELMO_INFORMATIONPRODUCT);
        return model.subjects().stream().map(Resource::stringValue).toArray(String[]::new);
    }
}
