package nl.dotWebly.data.integration;

import nl.dotWebly.api.Application;
import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.integration.configuration.SailMemoryTestConfiguration;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.data.repository.impl.SailMemoryRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Category(Categories.IntegrationTests.class)
@ContextConfiguration(classes = SailMemoryTestConfiguration.class)
public class TripleStoreClientTest {

    @Autowired
    TripleStoreRepository repository;

    @Autowired
    TripleStoreClient tripleStore;

    @Before
    public void clearData(){
        try(RepositoryConnection connection = repository.getConnection()) {
            connection.clear();
            connection.clearNamespaces();
        }
        finally {
            repository.shutDown();
        }
    }

    @Test
    public void testQuery() {
        //arrange
        Model pablo = createArtist("Picasso").build();
        Model ross = createArtist("Ross").build();

        addModelToStore(repository, pablo);
        addModelToStore(repository, ross);

        //act
        Model model = tripleStore.query();

        //assert
        Rio.write(model, System.out, RDFFormat.TURTLE);
        assertEquals("Size of statements should be four after saving ", 4, model.size());

        Model filterOnNames = model.filter(null, FOAF.LAST_NAME, null);

        assertEquals("There should be two last names", 2, filterOnNames.size());

        List<String> names = filterOnNames.objects().stream().map(v -> v.stringValue()).collect(toList());

        assertTrue("Names should contain Ross", names.contains("Ross"));
        assertTrue("Names should contain Picasso", names.contains("Picasso"));
    }

    @Test
    public void testQueryGroupedBySubject() {
        //arrange
        Model pablo = createArtist("Picasso").add(FOAF.FIRST_NAME, "Pablo").build();
        Model ross = createArtist("Ross").add(FOAF.FIRST_NAME, "Bob").build();

        addModelToStore(repository, pablo);
        addModelToStore(repository, ross);

        //act
        List<Model> models = tripleStore.queryGroupedBySubject();

        //assert
        models.forEach(model -> {
            Rio.write(model, System.out, RDFFormat.TURTLE);
        });

        assertEquals("Size should be two after saving ", 2, models.size());

        List<String> names = models.stream()
                .map(m -> m.filter(null, FOAF.LAST_NAME, null))
                .flatMap(m -> m.objects().stream())
                .map(m -> m.stringValue()).collect(toList());

        assertTrue("Names should be Ross", names.contains("Ross"));
        assertTrue("Names should be Picasso", names.contains("Picasso"));
    }

    private ModelBuilder createArtist(String artistName) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + artistName)
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, artistName);
    }

    private void addModelToStore(TripleStoreRepository repository, Model model) {
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(model);
            connection.commit();
        }
        finally {
            repository.shutDown();
        }
    }

}
