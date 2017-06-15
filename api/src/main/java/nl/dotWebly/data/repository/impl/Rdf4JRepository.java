package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.function.Consumer;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
public abstract class Rdf4JRepository implements TripleStoreRepository {

    public abstract Repository getRepository();

    @Override
    public void performQuery(Consumer<RepositoryConnection> performQuery) {
        Repository repository = getRepository();
        repository.initialize();

        try(RepositoryConnection connection = repository.getConnection()) {
            performQuery.accept(connection);
        }
        finally {
            repository.shutDown();
        }
    }
}
