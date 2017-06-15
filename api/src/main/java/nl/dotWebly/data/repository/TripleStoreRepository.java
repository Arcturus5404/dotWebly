package nl.dotWebly.data.repository;

import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.function.Consumer;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreRepository {

    void performQuery(Consumer<RepositoryConnection> performQuery);
}
