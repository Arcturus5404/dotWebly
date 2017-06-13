package nl.dotWebly.unit.data;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientClearAllTest {

    @Mock
    TripleStoreRepository repository;

    @Mock
    RepositoryConnection connection;

    @Mock
    ValueFactory valueFactory;

    @Mock
    IRI subjectIri;

    @InjectMocks
    private TripleStoreClient client = new TripleStoreClientImpl();

    @Test
    public void testClearAllCallsMethods() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getValueFactory()).thenReturn(valueFactory);

        //act
        client.clearAllTriples();

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
        verify(connection).clear();
        verify(connection).clearNamespaces();
    }
}
