package nl.dotWebly.data.client.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.data.client.TripleStoreClient;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Service
public class TripleStoreClientImpl implements TripleStoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(TripleStoreClientImpl.class);

    @Autowired
    TripleStoreRepository tripleStoreRepository;

    public void save(Model model) {
        doQuery(c -> c.add(model));
    }

    public Model query() {
        Model model = new LinkedHashModel();
        getStatements(result -> Iterations.addAll(result, model));
        return model;
    }

    /**
     * Query will query the triple store and return models grouped by subject.
     * @return
     */
    public List<Model> queryGroupedBySubject() {

        Map<Resource, Model> modelMap = new HashMap<>();
        getStatements(result -> {
            while (result.hasNext()) {
                Statement statement = result.next();
                Resource subject = statement.getSubject();

                if(!modelMap.containsKey(subject)) {
                    modelMap.put(subject, new LinkedHashModel());
                }

                modelMap.get(subject).add(statement);
            }
        });
        return new ArrayList<>(modelMap.values());
    }

    private void getStatements(Consumer<RepositoryResult<Statement>> consumer) {
        doQuery(connection -> {
                    try (RepositoryResult<Statement> result = connection.getStatements(null, null, null);) {
                        consumer.accept(result);
                    }
                }
        );
    }
    private void doQuery(Consumer<RepositoryConnection> connectionConsumer) {
        try (RepositoryConnection connection = tripleStoreRepository.getConnection()) {
            connectionConsumer.accept(connection);
        }
        finally {
            // before our program exits, make sure the database is properly shut down.
            tripleStoreRepository.shutDown();
        }
    }

}
