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
public class RdfMessageConverterCanWriteTest {

    @Test
    public void testCanWriteModels() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.valueOf("application/ld+json"));

        //assert
        assertTrue("It should be able to write models", canWrite);
    }

    @Test
    public void testCantWriteUnknownMediatypes() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.valueOf("application/xml"));

        //assert
        assertFalse("It should be able to write models with xml", canWrite);
    }
    @Test
    public void testCantWriteUnknownClass() {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);

        //act
        boolean canWrite = converter.canWrite(Object.class, MediaType.valueOf("application/ld+json"));

        //assert
        assertFalse("It should be able to write object", canWrite);
    }

}
