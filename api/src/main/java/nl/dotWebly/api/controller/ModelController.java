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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Model> getModels() {
        return ResponseEntity.ok(client.query());
    }

    @RequestMapping(path = "/{subject}")
    public ResponseEntity<Model> getModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String result = getNamespace(namespace);
        return ResponseEntity.ok(client.queryBySubject(result + subject));
    }

    @RequestMapping(path = "/{subject}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteModel(@PathVariable("subject") String subject, @RequestParam(value = "namespace", required = false) String namespace) {
        String result = getNamespace(namespace);
        client.deleteBySubject(result + subject);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Model> addModel(@RequestBody Model model) {
        Model result = client.add(model);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<Model> updateModel(@RequestBody Model model) {
        Model result = client.update(model);
        return ResponseEntity.ok(result);
    }

    @RequestMapping("/insert-testdata")
    public String insertTest() {

        ModelBuilder builder = new ModelBuilder();

        Model pablo = builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + "Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, "Picasso")
                .add(FOAF.FIRST_NAME, "Pablo").build();

        client.add(pablo);

        Model bob = builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + "Ross")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, "Ross")
                .add(FOAF.FIRST_NAME, "Bob").build();

        client.add(bob);

        return "done!";
    }

    private String getNamespace(String namespace) {
        return namespace == null ? defaultNamespace : namespace;
    }
}
