package nl.dotWebly.unit.api.converter;

import nl.dotWebly.api.converter.RdfMessageConverter;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfMessageConverterWriteTest {

    @Mock
    HttpOutputMessage message;

    @Mock
    OutputStream outputStream;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCanWriteModels() throws IOException {
        //arrange
        RdfMessageConverter converter = new RdfMessageConverter(RDFFormat.JSONLD);
        Model model = new LinkedHashModel();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, MediaType.valueOf("application/ld+json"), message);

        //assert
        verify(message, times(2)).getBody();
    }

}
