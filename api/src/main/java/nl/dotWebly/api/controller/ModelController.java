package nl.dotWebly.api.controller;

import nl.dotWebly.data.client.TripleStoreClient;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RestController
@RequestMapping("/api/v1/models")
public class ModelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    @Value("${default.namespace}")
    String defaultNamespace;

    @Autowired
    private TripleStoreClient client;

    @RequestMapping
    public Model getModels() {
        return client.query();
    }

    @RequestMapping("/{subject}")
    public Model getModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String resultingNamespace = getNamespace(namespace);
        return client.queryBySubject(resultingNamespace + subject);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String resultingNamespace = getNamespace(namespace);
        client.deleteBySubject(resultingNamespace + subject);
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

    private String getNamespace(String namespace) {
        return namespace == null ? defaultNamespace : namespace;
    }
}
