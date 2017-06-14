package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Repository
//@Primary
public class VirtuosoRepository implements TripleStoreRepository {

    private final virtuoso.rdf4j.driver.VirtuosoRepository repository;

    @Autowired
    public VirtuosoRepository(@Value("${connection.url}") String url,
                              @Value("${connection.user}") String user,
                              @Value("${connection.password}") String password,
                              @Value("${default.graph}") String graph) {
        repository = new virtuoso.rdf4j.driver.VirtuosoRepository(url,user,password,graph);
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
