package nl.dotWebly.api.controller;

import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
@Controller
@RequestMapping("/api/v1/informationproducts")
public class InformationProductController {

    private final InformationProductService service;

    @Autowired
    public InformationProductController(InformationProductService service) {
        this.service = service;
    }

    @RequestMapping
    public ResponseEntity<InformationProduct[]> getAllProducts() {
        return ResponseEntity.ok(service.getInformationProducts());
    }

}
