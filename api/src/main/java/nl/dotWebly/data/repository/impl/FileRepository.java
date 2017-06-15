package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@org.springframework.stereotype.Repository
public class FileRepository implements TripleStoreRepository {
    private Repository repository;

    @Autowired
    public FileRepository(@Value("${default.namespace}") String defaultNamespace, @Value("${init.file}") Optional<String[]> files)
            throws IOException {
        MemoryStore memoryStore = new MemoryStore();

        repository = new org.eclipse.rdf4j.repository.sail.SailRepository(memoryStore);
        repository.initialize();

        initialiseData(defaultNamespace, files.orElse(new String[0]));
    }

    private void initialiseData(String defaultNamespace, String[] files) throws IOException {
        Map<String, RDFFormat> fileFormats = getFileFormats();

        Model result = new LinkedHashModel();

        for (String fileName : files) {
            FileReader fileReader = new FileReader(fileName);
            String extension = FilenameUtils.getExtension(fileName);

            Model model = Rio.parse(fileReader, defaultNamespace, fileFormats.get(extension));
            result.addAll(model);
        }

        //add the model to the repo
        getConnection().add(result);
        shutDown();
    }

    @Override
    public RepositoryConnection getConnection() {
        return repository.getConnection();
    }

    @Override
    public void shutDown() {
        repository.shutDown();
    }

    private Map<String, RDFFormat> getFileFormats() {
        Field[] fields = RDFFormat.class.getDeclaredFields();

        Map<String, RDFFormat> formatMap = new HashMap<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && (RDFFormat.class == field.getDeclaringClass())) {
                try {
                    RDFFormat format = (RDFFormat) field.get(null);
                    for (String extension : format.getFileExtensions()) {
                        formatMap.put(extension, format);
                    }

                } catch (IllegalAccessException e) {
                    //Ignore this format
                }
            }
        }
        return formatMap;
    }
}
