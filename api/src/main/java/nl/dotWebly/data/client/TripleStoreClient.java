package nl.dotWebly.data.client;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreClient {

    /**
     * Query all triples in the datasource
     *
     * @return model with all statements from the datasource
     */
    Model query();


    /**
     * Query all triples with a given Subject IRI
     *
     * @return model with all statements from the datasource with the given subject
     */
    Model queryBySubject(String subject);

    /**
     * Query all triples in the datasource filtered by a combination of subject, predicate or object
     *
     * @return model with all statements from the datasource
     */
    <T> Model queryBy(String subject, String predicate, T object);

    /**
     * Query a select query by means of SPARQL
     *
     * @return model with all statements from the datasource
     */
    TupleQueryResult select(String query);

    /**
     * Query a construct or describe query by means of SPARQL
     *
     * @return model with all statements from the datasource
     */
    Model construct(String query);

    /**
     * Query an ask query or describe by means of SPARQL
     *
     * @return model with all statements from the datasource
     */
    boolean ask(String query);

    /**
     * Query all triples in the datasource and group them by subject
     *
     * @return models from the datasource, each with all statements by subject
     */
    List<Model> queryGroupedBySubject();

    /**
     * Adds the model with all statements to the datasource; all existing triples will be left alone
     *
     * @param model
     */
    Model add(Model model);


    /**
     * Updates/adds the model with all statements to the datasource;
     * All existing triples will be update when the subject and predicate match.
     * <p>
     * The old objects will be removed
     *
     * @param model
     */
    Model update(Model model);

    /**
     * Deletes all triples from the datasource
     */
    void clearAllTriples();

    /**
     * Delete a model with all statements as subject from the datasource
     *
     * @param subject
     */
    void deleteBySubject(String subject);
}
