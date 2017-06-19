package nl.dotWebly.unit.service;

import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import nl.dotWebly.data.service.impl.InformationProductServiceImpl;
import nl.dotWebly.data.utils.QueryUtils;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static nl.dotWebly.data.service.impl.InformationProductServiceImpl.*;
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
public class InformationProductServiceQueryTest extends InformationProductServiceBase {

    @Mock
    TripleStoreClientImpl<ConfigurationRepository> client;

    @Test(expected = IllegalStateException.class)
    public void productNotFound() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        LinkedHashModel model = new LinkedHashModel();
        when(client.construct(any())).thenReturn(model);

        //act
        service.queryInformationProduct("myQuery", null);
    }

    @Test
    public void returnsModel() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { ?s ?p ?o }").build();
        Model constructedResult = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(constructedResult);

        //act
        Model result = service.queryInformationProduct("name", null);

        //assert
        assertEquals("Result should be the same", constructedResult, result);
    }

    @Test
    public void noArguments() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { ?s ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        service.queryInformationProduct("name", null);

        //assert
        verify(client).construct("SELECT * WHERE { ?s ?p ?o }");
    }

    @Test
    public void convertOneArgument() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { <@PARAMETER@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER", "myParameter");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT * WHERE { <myParameter> ?p ?o }");
    }

    @Test
    public void convertOneArgumentMultipleTimes() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER@ WHERE { <@PARAMETER@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER", "myParameter");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT myParameter WHERE { <myParameter> ?p ?o }");
    }

    @Test
    public void convertTwoArguments() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER1@ WHERE { <@PARAMETER2@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER1", "myParameter1");
        parameters.put("PARAMETER2", "myParameter2");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT myParameter1 WHERE { <myParameter2> ?p ?o }");
    }

    @Test(expected = IllegalStateException.class)
    public void missingParameters() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER1@ WHERE { <@PARAMETER2@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER1", "myParameter1");
        service.queryInformationProduct("name", parameters);
    }
}
