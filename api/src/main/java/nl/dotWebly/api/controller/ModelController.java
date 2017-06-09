package nl.dotWebly.api.controller;

import nl.dotWebly.api.util.RDFFormats;
import nl.dotWebly.data.client.TripleStoreClient;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RestController
@RequestMapping("/api/v1")
public class ModelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelController.class);

    private TripleStoreClient client;

    @Autowired
    public ModelController(TripleStoreClient client) {
        this.client = client;
    }

    @RequestMapping("/models")
    public void getModels(HttpServletResponse response) {
        getModels(RDFFormat.JSONLD.getName(), response);
    }

    @RequestMapping("/models/{format}")
    public void getModels(@PathVariable("format") String format, HttpServletResponse response) {
        Model models = client.query();

        writeToOutputStream(response, models, RDFFormats.convert(format));
    }


    @RequestMapping("/models/insert-testdata")
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

    private void writeToOutputStream(HttpServletResponse response, Model models, RDFFormat format) {
        try {
            response.setContentType(format.getDefaultMIMEType());
            Rio.write(models, response.getOutputStream(), format);
        } catch (IOException e) {
            LOGGER.error("Error writing to the outputstream!");
        }
    }
}
