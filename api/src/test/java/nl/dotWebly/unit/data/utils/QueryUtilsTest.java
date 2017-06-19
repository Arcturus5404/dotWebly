package nl.dotWebly.unit.data.utils;

import nl.dotWebly.data.utils.QueryUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Rick Fleuren on 6/19/2017.
 */
public class QueryUtilsTest {

    @Test
    public void testNull() {
        //act
        String result = QueryUtils.addDefaultPrefixes(null);

        //assert
        assertNull("No result should be added", result);
    }
    @Test
    public void testNothingAdded() {
        //act
        String result = QueryUtils.addDefaultPrefixes("");

        //assert
        assertEquals("No result should be added", "", result);
    }

    @Test
    public void testNamespaceAdded() {
        //act
        String result = QueryUtils.addDefaultPrefixes("SELECT {?s rdf:type ?o}");

        //assert
        assertEquals("Results should be the same", "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nSELECT {?s rdf:type ?o}", result);
    }

    @Test
    public void testNamespaceIgnored() {
        //act
        String result = QueryUtils.addDefaultPrefixes("PREFIX rdf: <http://mynamespace.org/>\nSELECT {?s rdf:type ?o}");

        //assert
        assertEquals("Results should be the same", "PREFIX rdf: <http://mynamespace.org/>\nSELECT {?s rdf:type ?o}", result);
    }

    @Test
    public void testExpandNull() {
        //act
        String result = QueryUtils.expand(null);

        //assert
        assertNull("No prefix should be added", result);
    }

    @Test
    public void testExpandNoPrefix() {
        //act
        String result = QueryUtils.expand("String");

        //assert
        assertEquals("No prefix should be added", "String", result);
    }

    @Test
    public void testExpandAddPrefix() {
        //act
        String result = QueryUtils.expand("elmo:String");

        //assert
        assertEquals("Prefix should be added", "http://bp4mc2.org/elmo/def#String", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExpandUnknownPrefix() {
        //act
        QueryUtils.expand("unknown:String");
    }
}
