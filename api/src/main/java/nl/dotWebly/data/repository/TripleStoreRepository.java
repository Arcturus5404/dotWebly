package nl.dotWebly.data.repository;

import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreRepository {

    RepositoryConnection getConnection();

    void shutDown();
}
