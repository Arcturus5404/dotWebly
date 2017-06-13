package nl.dotWebly.data.client.impl;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Service
public class TripleStoreClientImpl implements TripleStoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(TripleStoreClientImpl.class);

    @Autowired
    TripleStoreRepository tripleStoreRepository;

    public Model add(Model model) {
        LinkedHashModel result = new LinkedHashModel();

        doQuery(c -> {
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
        });

        return result;
    }

    public Model update(Model model) {
        LinkedHashModel result = new LinkedHashModel();

        doQuery(c -> {

            //Check if the statement exist with subject / predicate
            model.forEach(s -> {
                    if(c.hasStatement(s.getSubject(), s.getPredicate(), null, true)) {
                        c.remove(s.getSubject(), s.getPredicate(), null);
                    }
                }
            );

            //add the new model to the triple store
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
        });

        return result;
    }

    @Override
    public Model query() {
        Model model = new LinkedHashModel();
        getStatements(result -> Iterations.addAll(result, model));
        return model;
    }

    @Override
    public Model queryBySubject(String subject) {
        assert subject != null;

        Model model = new LinkedHashModel();
        getStatementsBySubject(subject, result -> Iterations.addAll(result, model));
        return model;
    }

    @Override
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

    @Override
    public void deleteBySubject(String subject) {
        doQuery(connection -> {
            IRI subjectIri = connection.getValueFactory().createIRI(subject);
            connection.remove(subjectIri, null, null);
        });
    }

    private void getStatements(Consumer<RepositoryResult<Statement>> consumer) {
        getStatements(null, null, null, consumer);
    }

    private void getStatementsBySubject(String subject, Consumer<RepositoryResult<Statement>> consumer) {
        getStatements(subject, null, null, consumer);
    }

    private void getStatements(String subject, IRI predicate, Value object, Consumer<RepositoryResult<Statement>> consumer) {
        doQuery(connection -> {
                    ValueFactory factory = connection.getValueFactory();
                    Resource resourceSubject = subject != null ? factory.createIRI(subject) : null;

                    try (RepositoryResult<Statement> result = connection.getStatements(resourceSubject, predicate, object);) {
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

    private void getModelBySubject(Model model, LinkedHashModel result, RepositoryConnection c) {
        model.subjects().stream()
                .map(s -> c.getStatements(s, null, null))
                .forEach(s -> Iterations.addAll(s, result));
    }

}
