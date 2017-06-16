package nl.dotWebly.unit.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.SailMemoryTripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.*;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientQueryByTest extends TripleStoreClientTest {

    @Mock
    ValueFactory valueFactory;

    @Mock
    IRI subjectMock;

    @Mock
    IRI predicateMock;

    @Mock
    Literal objectMock;

    @Test
    public void testQueryByEmptyResult() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        Model model = client.queryBy(null, null, null);
        initConnectionConsumer();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model size should not be 0", model.size());
    }

    @Test
    public void testQueryByReturnsData() {
        //arrange
        Model picasso = createArtist("Picasso").build();
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new CollectionIteration(picasso)));

        //act
        Model model = client.queryBy(null,null,null);
        initConnectionConsumer();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model should not be 2", model.size());
    }

    @Test
    public void testQueryByCallsMethods() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        //act
        client.queryBy(null,null,null);
        initConnectionConsumer();

        //assert
        verify(connection).getStatements(null, null, null);
    }

    @Test
    public void convertsParameters() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI("subjectMock")).thenReturn(subjectMock);
        when(valueFactory.createIRI("predicateMock")).thenReturn(predicateMock);
        when(valueFactory.createLiteral("objectMock")).thenReturn(objectMock);

        //act
        client.queryBy("subjectMock","predicateMock","objectMock");
        initConnectionConsumer();

        //assert
        verify(connection).getStatements(subjectMock, predicateMock, objectMock);
    }

    @Test
    public void convertsObjectParametersToLiteralString() {
        runConversionTest("myString", (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralBoolean() {
        runConversionTest(true, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralInteger() {
        runConversionTest(42, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralDouble() {
        runConversionTest(42d, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralFloat() {
        runConversionTest(42f, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralLong() {
        runConversionTest(42l, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralBigDecimal() {
        runConversionTest(new BigDecimal(42), (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }
    @Test
    public void convertsObjectParametersToLiteralDate() {
        runConversionTest(new Date(42), (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
    }

    private <T> void runConversionTest(T objectToConvert, BiConsumer<ValueFactory, T> consumer) {
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        when(connection.getValueFactory()).thenReturn(valueFactory);
        consumer.accept(valueFactory, objectToConvert);

        //act
        client.queryBy(null,null, objectToConvert);
        initConnectionConsumer();

        //assert
        verify(connection).getStatements(null, null, objectMock);
    }

    @Test
    public void convertsObjectParametersToIRI() {
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        when(connection.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createIRI("http://example.org")).thenReturn(subjectMock);

        //act
        client.queryBy(null,null,"http://example.org");
        initConnectionConsumer();

        //assert
        verify(connection).getStatements(null, null, subjectMock);
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
