package nl.dotWebly.unit.data;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientQueryBySubjectTest {

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

    @Test(expected = AssertionError.class)
    public void testQueryByInvalidSubject() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI(anyString())).thenReturn(subjectIri);
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act & assert
        client.queryBySubject(null);
    }

    @Test
    public void testQueryBySubjectEmptyResult() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI(eq("subject"))).thenReturn(subjectIri);
        when(connection.getStatements(eq(subjectIri), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        Model model = client.queryBySubject("subject");

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model size should not be 0", model.size());
    }

    @Test
    public void testQueryBySubjectReturnsData() {
        //arrange
        when(repository.getConnection()).thenReturn(connection);
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI(eq("subject"))).thenReturn(subjectIri);

        Model picasso = createArtist("Picasso").build();
        Model ross = createArtist("Ross").build();

        List<Statement> statements = Stream.concat(picasso.stream(), ross.stream()).collect(toList());
        when(connection.getStatements(eq(subjectIri), any(), any())).thenReturn(new RepositoryResult<Statement>(new CollectionIteration<>(statements)));

        //act
        client.queryBySubject("subject");

        //assert
        verify(repository).getConnection();
        verify(repository).shutDown();
        verify(connection).getStatements(subjectIri, null, null);
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
