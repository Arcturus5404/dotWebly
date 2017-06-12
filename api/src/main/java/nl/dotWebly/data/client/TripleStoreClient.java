package nl.dotWebly.data.client;

import org.eclipse.rdf4j.model.Model;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreClient {

    void save(Model model);

    Model query();

    Model queryBySubject(String subject);

    List<Model> queryGroupedBySubject();

}
