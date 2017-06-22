package nl.dotWebly.unit.api.converter;

import nl.dotWebly.api.converter.RdfMessageConverter;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfMessageConverterCanReadTest {

    @Test
    public void testCanReadModels() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.valueOf("application/ld+json"));

        //assert
        assertTrue("It should be able to read models", canRead);
    }

    @Test
    public void testCantReadUnknownMediatypes() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.valueOf("application/xml"));

        //assert
        assertFalse("It should be able to read models with xml", canRead);
    }
    @Test
    public void testCantReadUnknownClass() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canRead = converter.canRead(Object.class, MediaType.valueOf("application/ld+json"));

        //assert
        assertFalse("It should be able to read object", canRead);
    }

}
