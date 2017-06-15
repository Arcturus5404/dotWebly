package nl.dotWebly.unit.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.SailMemoryTripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Consumer;

import static org.mockito.Mockito.verify;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public abstract class TripleStoreClientTest {
    @Mock
    TripleStoreRepository repository;

    @Mock
    RepositoryConnection connection;

    @Mock
    ValueFactory valueFactory;

    @InjectMocks
    protected TripleStoreClient client = new SailMemoryTripleStoreClient();

    @Captor
    private ArgumentCaptor<Consumer<RepositoryConnection>> connectionCaptor;

    protected void initConnectionConsumer() {
        //catch the consumer and pass the mock
        verify(repository).performQuery(connectionCaptor.capture());
        connectionCaptor.getValue().accept(connection);
    }
}
