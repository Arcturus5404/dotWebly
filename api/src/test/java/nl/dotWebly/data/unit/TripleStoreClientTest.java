package nl.dotWebly.data.unit;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientTest {

    @Mock
    TripleStoreRepository repository;

    @Mock
    RepositoryConnection connection;

    @InjectMocks
    private TripleStoreClient client = new TripleStoreClientImpl();

    @Test
    public void testQueryEmptyResult() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        Model model = client.query();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model size should not be 0", model.size());
    }

    @Test
    public void testQueryReturnsData() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);

        Model picasso = createArtist("Picasso").build();
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new CollectionIteration(picasso)));

        //act
        Model model = client.query();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model should not be 2", model.size());
    }

    @Test
    public void testQueryCallsMethods() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        client.query();

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
    }

    @Test
    public void testQueryGroupedBySubjectEmptyResult() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        List<Model> models = client.queryGroupedBySubject();

        //assert
        assertNotNull("Models size should not be 0", models.size());
    }

    @Test
    public void testQueryGroupedBySubjectCallsMethods() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        client.queryGroupedBySubject();

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
    }

    @Test
    public void testQueryGroupedBySubjectReturnsData() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        Model picasso = createArtist("Picasso").build();
        Model ross = createArtist("Ross").build();

        List<Statement> statements = Stream.concat(picasso.stream(), ross.stream()).collect(toList());
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new CollectionIteration<>(statements)));

        //act
        client.queryGroupedBySubject();

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
        verify(connection).getStatements(null, null, null);
    }

    @Test
    public void testSaveCallsMethods() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        Model picasso = createArtist("Picasso").build();

        //act
        client.save(picasso);

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
        verify(connection).add(picasso);
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
