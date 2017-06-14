package nl.dotWebly.api.controller;

import nl.dotWebly.data.client.TripleStoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
@Controller
@RequestMapping("/api/v1/informationproducts")
public class InformationProductController {

    private TripleStoreClient client;

    @Autowired
    public InformationProductController(TripleStoreClient client) {
        this.client = client;
    }

    @RequestMapping
    public ResponseEntity<String[]> getAllProducts() {
        return ResponseEntity.ok(new String[0]);
    }

}
