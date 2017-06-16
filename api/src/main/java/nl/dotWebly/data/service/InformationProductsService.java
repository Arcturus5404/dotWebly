package nl.dotWebly.data.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.GraphQueryResult;
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
    public InformationProduct[] getInformationProducts() {
        Model result = client.construct("");

        return null;//model.subjects().stream().map(this::toInformationProduct).toArray(InformationProduct[]::new);
    }
//
//    private InformationProduct toInformationProduct(Resource c) {
//        return new InformationProduct[0];
//    }

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
