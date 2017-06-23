package nl.dotWebly.unit.api.converter;

import nl.dotWebly.api.converter.RdfHtmlConverter;
import nl.dotWebly.api.converter.RdfRioMessageConverter;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertFalse;
import static nl.dotWebly.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfHtmlConverterTest {

    @Mock
    HttpOutputMessage message;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCanReadOtherMediaType() {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to read other media types", canRead);
    }

    @Test
    public void testCanRead() {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_XHTML_XML);

        //assert
        assertFalse("It shouldn't be able to read models", canRead);
    }

    @Test
    public void testCanWrite() {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_XHTML_XML);

        //assert
        assertTrue("It should be able to write models", canWrite);
    }

    @Test
    public void testCanWriteOtherMediaType() {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to write other media types", canWrite);
    }

    @Test
    public void testWrite() throws IOException {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, MediaType.APPLICATION_XHTML_XML, message);

        //assert
        verify(message, times(2)).getBody();
   }

    @Test
    public void testHtmlTags() throws IOException {
        //arrange
        RdfHtmlConverter converter = new RdfHtmlConverter();
        Model model = createArtist("Picasso").build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, MediaType.APPLICATION_XHTML_XML, message);

        //assert
        String html = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);

        assertTrue("Should contain html tag", html.contains("<html"));
        assertTrue("Should contain body tag", html.contains("<body"));
        assertTrue("Should contain table tag", html.contains("<table"));
        assertTrue("Should contain Subject header", html.contains("Subject"));
        assertTrue("Should contain Predicate header", html.contains("Predicate"));
        assertTrue("Should contain Object header", html.contains("Object"));
        assertTrue("Should contain Picasso data", html.contains("Picasso"));
    }

}
