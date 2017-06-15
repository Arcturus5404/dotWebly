package nl.dotWebly.data.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class InformationProductsService {
    private final TripleStoreClientImpl<ConfigurationRepository> client;

    public static final String ELMO_INFORMATIONPRODUCT = "http://bp4mc2.org/elmo/def#InformationProduct";

    @Autowired
    public InformationProductsService(TripleStoreClientImpl<ConfigurationRepository> client) {
        this.client = client;
    }

    /**
     * Get all the information products
     * @return information products names
     */
    public String[] getInformationProducts() {
        Model model = client.queryBy(null, null, ELMO_INFORMATIONPRODUCT);
        return model.subjects().stream().map(Resource::stringValue).toArray(String[]::new);
    }

    /**
     * Get the meta data for the information product
     * @param informationProduct
     * @return
     */
    public String[] getMetadata(String informationProduct) {
        //get all information products
        Model model = client.queryBy(null, null, ELMO_INFORMATIONPRODUCT);
        return model.subjects().stream().map(Resource::stringValue).toArray(String[]::new);
    }
}
