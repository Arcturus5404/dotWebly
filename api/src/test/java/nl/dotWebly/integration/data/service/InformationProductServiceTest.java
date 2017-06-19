package nl.dotWebly.integration.data.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import nl.dotWebly.data.utils.QueryUtils;
import nl.dotWebly.integration.data.client.configuration.SailMemoryTestConfiguration;
import nl.dotWebly.integration.data.service.configuration.ServiceConfiguration;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.List;

import static nl.dotWebly.data.service.InformationProductService.ELMO_INFORMATIONPRODUCT;
import static nl.dotWebly.data.service.InformationProductService.ELMO_QUERY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(Categories.IntegrationTests.class)
@ContextConfiguration(classes = ServiceConfiguration.class)
public class InformationProductServiceTest {

    @Autowired
    protected TripleStoreClientImpl<ConfigurationRepository> tripleStore;

    @Before
    public void before() {
        tripleStore.clearAllTriples();
    }

    @Test
    public void testQuery() {
        //arrange
        InformationProductService service = new InformationProductService(tripleStore);
        Model informationProduct = createInformationProduct("name", ELMO_INFORMATIONPRODUCT,"query").build();
        tripleStore.add(informationProduct);

        //act
        List<InformationProduct> products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 1", 1, products.size());

        InformationProduct product = products.get(0);
        assertEquals("Name should be name", "http://example.org/name", product.getName());
        assertEquals("Query should be query", "query", product.getQuery());
        assertEquals("No parameters", 0, product.getParameters().size());
    }

    @Test
    public void testEmptyQueryObject() {
        //arrange
        InformationProductService service = new InformationProductService(tripleStore);
        Model informationProduct = createInformationProduct("name",  ELMO_INFORMATIONPRODUCT,null).build();
        tripleStore.add(informationProduct);

        //act
        List<InformationProduct> products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 1", 1, products.size());

        InformationProduct product = products.get(0);
        assertEquals("Name should be name", "http://example.org/name", product.getName());
        assertNull("Query should be null", product.getQuery());
    }

    @Test
    public void testIncorrectData() {
        //arrange
        InformationProductService service = new InformationProductService(tripleStore);
        Model informationProduct = createInformationProduct("name",  "elmo:Othertype", null).build();
        tripleStore.add(informationProduct);

        //act
        List<InformationProduct> products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 0, because its not an information product", 0, products.size());
    }

    private ModelBuilder createInformationProduct(String name, String type, String query) {

        ModelBuilder builder = new ModelBuilder();

        builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + name)
                .add(RDF.TYPE, SimpleValueFactory.getInstance().createIRI(QueryUtils.expand(type)))
                .add(FOAF.FIRST_NAME, "MyName");

        if(query != null) {
            builder.add(QueryUtils.expand(ELMO_QUERY), query);
        }

        return builder;
    }
}
