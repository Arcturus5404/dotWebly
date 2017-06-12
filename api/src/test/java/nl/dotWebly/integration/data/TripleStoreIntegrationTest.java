package nl.dotWebly.integration.data;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.integration.data.configuration.SailMemoryTestConfiguration;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Category(Categories.IntegrationTests.class)
@ContextConfiguration(classes = SailMemoryTestConfiguration.class)
public abstract class TripleStoreIntegrationTest {

    @Autowired
    protected TripleStoreRepository repository;

    @Autowired
    protected TripleStoreClient tripleStore;

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

    // Helper methods
    protected ModelBuilder createArtist(String artistName) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + artistName)
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, artistName);
    }

    protected void addModelToStore(Model model) {
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(model);
            connection.commit();
        }
        finally {
            repository.shutDown();
        }
    }

    // Helper methods
    protected Model getStatementsFromStore() {
        try (RepositoryConnection connection = repository.getConnection()) {
            Model result = new LinkedHashModel();
            Iterations.addAll(connection.getStatements(null, null, null), result);
            return result;
        }
        finally {
            repository.shutDown();
        }
    }
}
