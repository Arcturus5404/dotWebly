package nl.dotWebly.unit.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.IRI;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientDeleteTest extends TripleStoreClientTest {

    @Mock
    IRI subjectIri;

    @Test
    public void testDeleteCallsMethods() {
        //arrange
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI(eq("subject"))).thenReturn(subjectIri);

        //act
        client.deleteBySubject("subject");
        initConnectionConsumer();

        //assert
        verify(connection).remove(subjectIri, null, null);
    }
}
