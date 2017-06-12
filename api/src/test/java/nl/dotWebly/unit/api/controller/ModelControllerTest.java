package nl.dotWebly.unit.api.controller;

import nl.dotWebly.api.controller.ModelController;
import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class ModelControllerTest {

    @Mock
    TripleStoreClient client;

    @InjectMocks
    private ModelController controller = new ModelController();

    @Test
    public void testModels() {
        //arrange
        LinkedHashModel model = new LinkedHashModel();
        when(client.query()).thenReturn(model);

        //act
        Model result = controller.getModels();

        //assert
        assertEquals("Models should be the same", model, result);
    }

    @Test
    public void testModelBySubject() {
        //arrange
        LinkedHashModel model = new LinkedHashModel();
        when(client.queryBySubject(eq("subject"))).thenReturn(model);

        //act
        Model result = controller.getModelBySubject("subject");

        //assert
        assertEquals("Models should be the same", model, result);
    }

}
