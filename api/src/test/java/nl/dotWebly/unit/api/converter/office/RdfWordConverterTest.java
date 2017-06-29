package nl.dotWebly.unit.api.converter.office;

import nl.dotWebly.api.converter.office.RdfWordConverter;
import nl.dotWebly.test.categories.Categories;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertFalse;
import static nl.dotWebly.utils.TestUtils.createArtist;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfWordConverterTest {

    final MediaType mediaType = MediaType.valueOf("application/msword");

    @Mock
    HttpOutputMessage message;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCantReadOtherMediaType() {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to read other media types", canRead);
    }

    @Test
	@Ignore
    public void testCantRead() {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

        //assert
        assertFalse("It shouldn't be able to read models", canRead);
    }

    @Test
	@Ignore
    public void testCanWrite() {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

        //assert
        assertTrue("It should be able to write models", canWrite);
    }

    @Test
	@Ignore
    public void testCanWriteOtherMediaType() {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to write other media types", canWrite);
    }

    @Test
	@Ignore
    public void testWrite() throws IOException {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        verify(message, times(2)).getBody();
   }

    @Test
	@Ignore
    public void testAddsHeader() throws IOException {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        verify(headers).add("Content-Disposition", "attachment; filename=data.doc");
   }

    @Test

    public void testWordTags() throws IOException {
        //arrange
        RdfWordConverter converter = new RdfWordConverter();
        Model model = createArtist("Picasso").build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        HWPFDocument document = new HWPFDocument(new ByteArrayInputStream(outputStream.toByteArray()));
        WordExtractor extractor = new WordExtractor(document);

        String word = extractor.getText();
        assertTrue("Should contain Picasso data", word.contains("Picasso"));
        assertTrue("Should contain Artist data", word.contains("Artist"));
    }

}
