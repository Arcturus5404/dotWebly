package nl.dotWebly.data.client.impl;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.repository.TripleStoreRepository;
import org.apache.commons.lang3.ClassUtils;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public abstract class TripleStoreClientImpl<R extends TripleStoreRepository> implements TripleStoreClient {

    private static final Logger LOG = LoggerFactory.getLogger(TripleStoreClientImpl.class);

    @Autowired
    private R repository;

    public Model add(Model model) {
        return repository.performQuery(c -> {
            LinkedHashModel result = new LinkedHashModel();
            c.add(model);

            //Get all the statements which belong to the subject:
            getModelBySubject(model, result, c);
            return result;
        });
    }

    public Model update(Model model) {
        return repository.performQuery(c -> {
            LinkedHashModel result = new LinkedHashModel();
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
            return result;
        });
    }

    @Override
    public Model query() {
        return getAllStatementsAsModel();
    }

    @Override
    public Model queryBySubject(String subject) {
        assert subject != null;
        return getStatementsAsModel(subject, null, null);
    }

    @Override
    public <T> Model queryBy(String subject, String predicate, T object) {
        return getStatementsAsModel(subject, predicate, object);
    }

    @Override
    public boolean ask(String query) {
        return repository.performQuery(c -> {
            BooleanQuery booleanQuery = c.prepareBooleanQuery(query);
            return booleanQuery.evaluate();
        });
    }
    @Override
    public TupleQueryResult select(String query) {
        return repository.performQuery(c -> {
            TupleQuery tupleQuery = c.prepareTupleQuery(query);
            return tupleQuery.evaluate();
        });
    }
    @Override
    public Model construct(String query) {
        return repository.performQuery(c -> {
            Model model = new LinkedHashModel();
            GraphQuery graphQuery = c.prepareGraphQuery(query);
            GraphQueryResult result = graphQuery.evaluate();

            Iterations.addAll(result, model);
            return model;
        });
    }

    @Override
    public List<Model> queryGroupedBySubject() {
        return getStatements(null, null, null, result -> {
            Map<Resource, Model> modelMap = new HashMap<>();
            while (result.hasNext()) {
                Statement statement = result.next();
                Resource subject = statement.getSubject();

                if(!modelMap.containsKey(subject)) {
                    modelMap.put(subject, new LinkedHashModel());
                }

                modelMap.get(subject).add(statement);
            }
            return new ArrayList<>(modelMap.values());
        });
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

    private Model getAllStatementsAsModel() {
        return getStatementsAsModel(null, null, null);
    }

    private Model getStatementsAsModel(String subject, String predicate, Object object) {
        return getStatements(subject, predicate, object, result -> Iterations.addAll(result, new LinkedHashModel()));
    }

    private <T> T getStatements(String subject, String predicate, Object object, Function<RepositoryResult<Statement>, T> consumer) {
        return repository.performQuery(connection -> {
                    ValueFactory factory = connection.getValueFactory();
                    Resource resourceSubject = subject != null ? factory.createIRI(subject) : null;
                    IRI iriPredicate = predicate != null ? factory.createIRI(predicate) : null;
                    Value valueObject = object != null ? createValue(object, factory) : null;

                    try (RepositoryResult<Statement> result = connection.getStatements(resourceSubject, iriPredicate, valueObject)) {
                        return consumer.apply(result);
                    }
                }
        );
    }

    private Value createValue(Object object, ValueFactory factory) {
        if (object == null) {
            return null;
        }

        if (object instanceof String && ((String) object).toLowerCase().startsWith("http://")) {
            String strObj = (String) object;
            return factory.createIRI(strObj);
        } else {
            return createLiteral(object, factory);
        }
    }

    private <T> Value createLiteral(T object, ValueFactory factory) {
        try {
            Class argument = ClassUtils.isPrimitiveWrapper(object.getClass()) ? ClassUtils.wrapperToPrimitive(object.getClass()) : object.getClass();
            Method createMethod = factory.getClass().getMethod("createLiteral", argument);
            return (Value) createMethod.invoke(factory, object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("This type was not supported: " + object.getClass(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("This type was not supported" + object.getClass(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("This type was not supported" + object.getClass(), e);
        }
    }

    private void getModelBySubject(Model model, LinkedHashModel result, RepositoryConnection c) {
        model.subjects().stream()
                .map(s -> c.getStatements(s, null, null))
                .forEach(s -> Iterations.addAll(s, result));
    }

}
