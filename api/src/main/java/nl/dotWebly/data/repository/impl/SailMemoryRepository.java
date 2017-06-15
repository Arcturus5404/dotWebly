package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.util.Optional;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@org.springframework.stereotype.Repository
@Primary
public class SailMemoryRepository implements TripleStoreRepository {
    private Repository repository;

    public SailMemoryRepository() {
        this(Optional.of(Boolean.FALSE), Optional.empty());
    }

    @Autowired
    public SailMemoryRepository(@Value("${init.clearData}") Optional<Boolean> clearData, @Value("${init.filepath}") Optional<String> filePath) {
        MemoryStore memoryStore = filePath.isPresent() && !"".equals(filePath.get())
                ? new MemoryStore(new File(filePath.get()))
                : new MemoryStore();

        memoryStore.setPersist(true);

        repository = new org.eclipse.rdf4j.repository.sail.SailRepository(memoryStore);
        repository.initialize();

        if(clearData.orElse(Boolean.FALSE)) {
            clearAllData();
        }
    }

    private void clearAllData() {
        try(RepositoryConnection connection = getConnection()) {
            connection.clear();
            connection.clearNamespaces();
        }
        finally {
            repository.shutDown();
        }
    }

    @Override
    public RepositoryConnection getConnection() {
        return repository.getConnection();
    }
    @Override
    public void shutDown() {
        repository.shutDown();
    }
}
