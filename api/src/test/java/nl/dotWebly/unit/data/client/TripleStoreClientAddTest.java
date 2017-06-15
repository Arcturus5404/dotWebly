package nl.dotWebly.unit.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientAddTest {

    @Mock
    TripleStoreRepository repository;

    @Mock
    RepositoryConnection connection;

    @InjectMocks
    private TripleStoreClient client = new TripleStoreClientImpl();

    @Test
    public void testAddCallsMethods() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        Model picasso = createArtist("Picasso").build();

        //act
        client.add(picasso);

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
        verify(connection).add(picasso);

        ArgumentCaptor<IRI> getSubject = ArgumentCaptor.forClass(IRI.class);
        verify(connection).getStatements(getSubject.capture(), eq(null), eq(null));
        assertEquals("IRI should be http://example.org/Picasso", "http://example.org/Picasso", getSubject.getValue().toString());
    }

    @Test
    public void testUpdateWithExistingData() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        Model existingPicasso = createArtist("Picasso")
                .add(FOAF.LAST_NAME, "OtherName")
                .build();
        Model picasso = createArtist("Picasso").build();

        when(connection.getStatements(any(), any(), any()))
                .thenReturn(new RepositoryResult<>(new CollectionIteration<>(existingPicasso)))
                .thenReturn(new RepositoryResult<>(new CollectionIteration<>(picasso)));

        //First, ex:Artist, then ex:Lastname, the rest
        when(connection.hasStatement(any(), any(), any(), eq(true))).thenReturn(false, true, false);

        //act
        client.update(picasso);

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();

        ArgumentCaptor<IRI> removeSubject = ArgumentCaptor.forClass(IRI.class);
        ArgumentCaptor<IRI> removePredicate = ArgumentCaptor.forClass(IRI.class);
        ArgumentCaptor<IRI> removeValue = ArgumentCaptor.forClass(IRI.class);
        verify(connection).remove(removeSubject.capture(), removePredicate.capture(), removeValue.capture());
        assertEquals("Subject should be http://example.org/Picasso", "http://example.org/Picasso", removeSubject.getValue().toString());
        assertEquals("Predicate should be type http://xmlns.com/foaf/0.1/lastName", "http://xmlns.com/foaf/0.1/lastName", removePredicate.getValue().toString());
        assertNull("Object should be null", removeValue.getValue());

        verify(connection).add(picasso);

        ArgumentCaptor<IRI> getSubject = ArgumentCaptor.forClass(IRI.class);
        verify(connection).getStatements(getSubject.capture(), eq(null), eq(null));
        assertEquals("IRI should be http://example.org/Picasso", "http://example.org/Picasso", getSubject.getValue().toString());
    }

    private ModelBuilder createArtist(String artistName) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + artistName)
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, artistName);
    }
}
