package nl.dotWebly.unit.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static nl.dotWebly.data.service.InformationProductService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */

@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class InformationProductServiceTest {

    @Mock
    TripleStoreClientImpl<ConfigurationRepository> client;

    @Test
    public void getInformationProducts() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        when(client.construct(any())).thenReturn(new LinkedHashModel());

        //act
        InformationProduct[] result = service.getInformationProducts();

        //assert
        assertEquals("Size should be 0", 0, result.length);
    }

    @Test
    public void usesQuery() {
        //arrange
        InformationProductService service = new InformationProductService(client);

        String query = String.format(CONSTRUCT_QUERY, ELMO_INFORMATIONPRODUCT);
        when(client.construct(eq(query))).thenReturn(new LinkedHashModel());

        //act
        service.getInformationProducts();

        //assert
        verify(client).construct(query);
    }


    @Test
    public void extractsInformationProduct() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct = createInformationProduct("name", "query").build();
        when(client.construct(any())).thenReturn(informationProduct);

        //act
        InformationProduct[] products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 1", 1, products.length);
        assertEquals("Name should be name", "http://example.org/name", products[0].getName());
        assertEquals("Query should be query", "query", products[0].getQuery());
        assertEquals("No parameters", 0, products[0].getParameters().size());
    }

    @Test
    public void extractsParametersFromQuery() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct = createInformationProduct("name", "query @p1@ @p2@ @p3@ @p1@").build();
        when(client.construct(any())).thenReturn(informationProduct);

        //act
        InformationProduct[] products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 1", 1, products.length);
        assertEquals("Three parameters", 3, products[0].getParameters().size());
    }

    @Test
    public void extractsSeveralInformationProducts() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct1 = createInformationProduct("name1", "query").build();
        Model informationProduct2 = createInformationProduct("name2", "query").build();
        when(client.construct(any())).thenReturn(new LinkedHashModel(Stream.concat(informationProduct1.stream(), informationProduct2.stream()).collect(toList())));

        //act
        InformationProduct[] products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 2", 2, products.length);
    }

    private ModelBuilder createInformationProduct(String name, String query) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + name)
                .add(RDF.TYPE, ELMO_INFORMATIONPRODUCT)
                .add(ELMO_QUERY, query);
    }
}
