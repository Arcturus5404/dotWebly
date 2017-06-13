package nl.dotWebly.data.client;

import org.eclipse.rdf4j.model.Model;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreClient {


    /**
     * Query all triples in the datasource
     * @return model with all statements from the datasource
     */
    Model query();


    /**
     * Query all triples with a given Subject IRI
     * @return model with all statements from the datasource with the given subject
     */
    Model queryBySubject(String subject);


    /**
     * Query all triples in the datasource and group them by subject
     * @return models from the datasource, each with all statements by subject
     */
    List<Model> queryGroupedBySubject();

    /**
     * Adds the model with all statements to the datasource; all existing triples will be left alone
     * @param model
     */
    Model add(Model model);


    /**
     * Updates/adds the model with all statements to the datasource;
     * All existing triples will be update when the subject and predicate match.
     *
     * The old objects will be removed
     * @param model
     */
    Model update(Model model);

    /**
     * Delete a model with all statements as subject from the datasource
     * @param subject
     * @return
     */
    void deleteBySubject(String subject);
}
