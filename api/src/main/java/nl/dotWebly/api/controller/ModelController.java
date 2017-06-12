package nl.dotWebly.api.controller;

import nl.dotWebly.data.client.TripleStoreClient;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RestController
@RequestMapping("/api/v1/models")
public class ModelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    private TripleStoreClient client;

    @Autowired
    public ModelController(TripleStoreClient client) {
        this.client = client;
    }

    @RequestMapping
    public Model getModels() {
        Model model = client.query();
        return model;
    }

    @RequestMapping("/insert-testdata")
    public String insertTest() {

        ModelBuilder builder = new ModelBuilder();

        Model model = builder
            .setNamespace("ex", "http://example.org/")
            .subject("ex:" + "Picasso")
            .add(RDF.TYPE, "ex:Artist")
            .add(FOAF.LAST_NAME, "Picasso")
            .add(FOAF.FIRST_NAME, "Pablo").build();

        client.save(model);

        return "done!";
    }
}
