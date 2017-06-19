package nl.dotWebly.unit.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import nl.dotWebly.data.utils.QueryUtils;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
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
        when(client.select(any())).thenReturn(convertModel(new LinkedHashModel()));

        //act
        List<InformationProduct> result = service.getInformationProducts();

        //assert
        assertEquals("Size should be 0", 0, result.size());
    }

    @Test
    public void usesQuery() {
        //arrange
        InformationProductService service = new InformationProductService(client);

        String query = String.format(SELECT_QUERY, ELMO_INFORMATIONPRODUCT);
        when(client.select(eq(query))).thenReturn(convertModel(new LinkedHashModel()));

        //act
        service.getInformationProducts();

        //assert
        verify(client).select(query);
    }

    @Test
    public void extractsInformationProduct() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct = createInformationProduct("name", "query").build();
        when(client.select(any())).thenReturn(convertModel(informationProduct));

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
    public void extractsParametersFromQuery() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct = createInformationProduct("name", "query @p1@ @p2@ @p3@ @p1@").build();
        when(client.select(any())).thenReturn(convertModel(informationProduct));

        //act
        List<InformationProduct> products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 1", 1, products.size());
        assertEquals("Three parameters", 3, products.get(0).getParameters().size());
    }

    @Test
    public void extractsSeveralInformationProducts() {
        //arrange
        InformationProductService service = new InformationProductService(client);
        Model informationProduct1 = createInformationProduct("name1", "query").build();
        Model informationProduct2 = createInformationProduct("name2", "query").build();
        when(client.select(any())).thenReturn(
                convertModel(new LinkedHashModel(concat(informationProduct1.stream(), informationProduct2.stream()).collect(toList()))));

        //act
        List<InformationProduct> products = service.getInformationProducts();

        //assert
        assertEquals("Size should be 2", 2, products.size());
    }

    private List<Map<String, Value>> convertModel(Model model) {
        return model.stream().map(s -> {
            Map<String, Value> result = new HashMap<>();
            result.put("s", s.getSubject());
            result.put("p", s.getPredicate());
            result.put("o", s.getObject());
            return result;
        }).collect(toList());
    }

    private ModelBuilder createInformationProduct(String name, String query) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + name)
                .add(RDF.TYPE, QueryUtils.expand(ELMO_INFORMATIONPRODUCT))
                .add(QueryUtils.expand(ELMO_QUERY), query);
    }
}
