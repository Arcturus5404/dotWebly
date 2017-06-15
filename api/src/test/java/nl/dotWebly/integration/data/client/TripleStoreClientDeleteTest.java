package nl.dotWebly.integration.data.client;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class TripleStoreClientDeleteTest extends TripleStoreIntegrationTest {
    @Test
    public void testDelete() {
        //arrange
        Model pablo = createArtist("Picasso").build();
        addModelToStore(pablo);
        Model model = getStatementsFromStore();
        assertEquals("There should be 2 statements, all should be stored first", 2, model.size());

        //act
        tripleStore.deleteBySubject("http://example.org/Picasso");

        //assert
        Model result = getStatementsFromStore();
        assertEquals("There should be 0 statements, all should be removed", 0, result.size());
    }
}
