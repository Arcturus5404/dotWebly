package nl.dotWebly.api.controller;

import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductsService;
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

    private final InformationProductsService service;

    @Autowired
    public InformationProductController(InformationProductsService service) {
        this.service = service;
    }

    @RequestMapping
    public ResponseEntity<InformationProduct[]> getAllProducts() {
        return ResponseEntity.ok(service.getInformationProducts());
    }

}
