package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Repository
public class VirtuosoRepository implements TripleStoreRepository {

    private final virtuoso.rdf4j.driver.VirtuosoRepository repository;

    public VirtuosoRepository() {
        repository = new virtuoso.rdf4j.driver.VirtuosoRepository("jdbc:virtuoso://localhost:1111","dba","dba");
        repository.initialize();
    }

    @Override
    public RepositoryConnection getConnection() {
        return repository.getConnection();
    }

    @Override
    public void shutDown() {
        repository.shutDown();
    }
}
