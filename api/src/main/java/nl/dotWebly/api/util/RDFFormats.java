package nl.dotWebly.api.util;

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class RDFFormats {

    public static final String JSONLD = "JSON-LD";
    public static final String TURTLE = "TURTLE";
    public static final String RDFXML = "RDFXML";
    public static final String RDFJSON = "RDFJSON";

    public static RDFFormat convert(String format) {

        switch (format.toUpperCase()) {
            case JSONLD:
                return RDFFormat.JSONLD;
            case RDFJSON:
                return RDFFormat.RDFJSON;
            case TURTLE:
                return RDFFormat.TURTLE;
            case RDFXML:
                return RDFFormat.RDFXML;
            default:
                throw new IllegalArgumentException(String.format("Format '%s' not supported", format));
        }
    }
}
