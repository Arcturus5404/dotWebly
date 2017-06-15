package nl.dotWebly.unit.data.repository;

import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.data.repository.impl.FileRepository;
import nl.dotWebly.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class FileRepositoryTest {

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    Resource resource;

    private String pabloTtl =
        "<http://example.org/Picasso> a <http://example.org/Artist> ;\n" +
            "\t<http://xmlns.com/foaf/0.1/firstName> \"Pablo\" ;\n" +
            "\t<http://xmlns.com/foaf/0.1/lastName> \"Picasso\" .";

    @Test
    public void testInitialize() throws IOException {
        //arrange
        FileRepository repository = new FileRepository(resourceLoader, "testfile.data","test.data");

        //act
        Model result = getModels(repository);

        //assert
        assertEquals("There should have been no files loaded", 0, result.size());
    }

    @Test
    public void testLoadTtlFile() throws IOException {
        //arrange
        when(resourceLoader.getResource(eq("pablo.ttl"))).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(pabloTtl.getBytes(StandardCharsets.UTF_8)));
        FileRepository repository = new FileRepository(resourceLoader, "", "test.data","pablo.ttl");

        //act
        Model result = getModels(repository);

        //assert
        assertEquals("There should have been 3 statements added to the in memory database", 3, result.size());
        verify(resourceLoader).getResource("pablo.ttl");
        verify(resource).getInputStream();
    }

    private Model getModels(TripleStoreRepository repository) {
        Model result = new LinkedHashModel();

        repository.performQuery(connection -> Iterations.addAll(connection.getStatements(null, null, null), result));

        return result;
    }
}
