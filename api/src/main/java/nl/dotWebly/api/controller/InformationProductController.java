package nl.dotWebly.api.controller;

import nl.dotWebly.data.service.InformationProduct;
import nl.dotWebly.data.service.InformationProductService;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
@Controller
@RequestMapping("/api/v1/informationproducts")
public class InformationProductController {

    @Value("${default.namespace}")
    String defaultNamespace;

    private final InformationProductService service;

    @Autowired
    public InformationProductController(InformationProductService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<InformationProduct>> getInformationProducts() {
        return ResponseEntity.ok(service.getInformationProducts());
    }

    @RequestMapping(path = "/{name}", method = RequestMethod.GET)
    public ResponseEntity<InformationProduct> getInformationProduct(@PathVariable("name") String name, @RequestParam("namespace") String namespace) {
        Optional<InformationProduct> product = service.getInformationProduct(getNamespace(namespace) + name);
        if(product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{name}/query", method = RequestMethod.GET)
    public ResponseEntity<Model> queryInformationProduct(@PathVariable("name") String name, @RequestParam("namespace") String namespace, HttpServletRequest request) {
        Map<String, String[]> parametersMap = request.getParameterMap();
        Map<String, String> parameters = parametersMap.keySet().stream().collect(toMap(s -> s, s -> parametersMap.get(s)[0]));

        return ResponseEntity.ok(service.queryInformationProduct(getNamespace(namespace) + name, parameters));
    }

    private String getNamespace(String namespace) {
        return namespace == null ? defaultNamespace : namespace;
    }
}
