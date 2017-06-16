package nl.dotWebly.unit.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.SailMemoryTripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    private ArgumentCaptor<Consumer<RepositoryConnection>> consumerCaptor;

    @Captor
    private ArgumentCaptor<Function<RepositoryConnection, ?>> functionCaptor;

    protected void initConnectionConsumer() {
        doAnswer(invocation -> {
            ((Consumer<RepositoryConnection>) invocation.getArguments()[0]).accept(connection);
            return null;
        }).when(repository).performQuery((Consumer<RepositoryConnection>) any());
    }

    protected void initConnectionFunction() {
        when(repository.performQuery((Function<RepositoryConnection, ?>) any())).thenAnswer(
                new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                return ((Function<RepositoryConnection, ? extends Object>) invocation.getArguments()[0]).apply(connection);
            }});
    }
}
