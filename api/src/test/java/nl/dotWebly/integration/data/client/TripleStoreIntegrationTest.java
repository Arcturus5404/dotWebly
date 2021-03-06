package nl.dotWebly.integration.data.client;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.integration.data.client.configuration.SailMemoryTestConfiguration;
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
    public void clearData() {
        repository.performQuery(connection -> {
            connection.clear();
            connection.clearNamespaces();
        });
    }

    // Helper methods
    protected void addModelToStore(Model model) {
        repository.performQuery(connection -> {
            connection.add(model);
            connection.commit();
        });
    }

    // Helper methods
    protected Model getStatementsFromStore() {
        return repository.performQuery((RepositoryConnection connection) -> Iterations.addAll(connection.getStatements(null, null, null), new LinkedHashModel()));
    }
}
