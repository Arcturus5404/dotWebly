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
public abstract class TripleStoreClientImpl<R extends TripleStoreRepository> implements TripleStoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(TripleStoreClientImpl.class);

    @Autowired
    private R repository;

    public Model add(Model model) {
        LinkedHashModel result = new LinkedHashModel();

        repository.performQuery(c -> {
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
        });

        return result;
    }

    public Model update(Model model) {
        LinkedHashModel result = new LinkedHashModel();

        repository.performQuery(c -> {

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
        getStatements(subject, null, null, result -> Iterations.addAll(result, model));
        return model;
    }

    @Override
    public Model queryBy(String subject, String predicate, String object) {
        Model model = new LinkedHashModel();
        getStatements(subject, predicate, object, result -> Iterations.addAll(result, model));
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
        repository.performQuery(connection -> {
            IRI subjectIri = connection.getValueFactory().createIRI(subject);
            connection.remove(subjectIri, null, null);
        });
    }

    @Override
    public void clearAllTriples() {
        repository.performQuery(connection -> {
            connection.clear();
            connection.clearNamespaces();
        });
    }

    private void getStatements(Consumer<RepositoryResult<Statement>> consumer) {
        getStatements(null, null, null, consumer);
    }

    private void getStatements(String subject, String predicate, String object, Consumer<RepositoryResult<Statement>> consumer) {
        repository.performQuery(connection -> {
                    ValueFactory factory = connection.getValueFactory();
                    Resource resourceSubject = subject != null ? factory.createIRI(subject) : null;
                    IRI iriPredicate = subject != null ? factory.createIRI(predicate) : null;
                    Value valueObject = object != null ? factory.createBNode(object) : null;

                    try (RepositoryResult<Statement> result = connection.getStatements(resourceSubject, iriPredicate, valueObject)) {
                        consumer.accept(result);
                    }
                }
        );
    }

    private void getModelBySubject(Model model, LinkedHashModel result, RepositoryConnection c) {
        model.subjects().stream()
                .map(s -> c.getStatements(s, null, null))
                .forEach(s -> Iterations.addAll(s, result));
    }

}
