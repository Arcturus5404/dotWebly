package nl.dotWebly.data.repository.impl;

import nl.dotWebly.data.repository.TripleStoreRepository;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.File;
import java.util.Optional;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@org.springframework.stereotype.Repository
public class SailMemoryRepository implements TripleStoreRepository {
    private Repository repository;

    public SailMemoryRepository() {
        this(false, Optional.empty());
    }

    public SailMemoryRepository(boolean clearData, Optional<String> filePath) {
        MemoryStore memoryStore = filePath.isPresent() ? new MemoryStore(new File(filePath.get())) : new MemoryStore();
        memoryStore.setPersist(true);

        repository = new org.eclipse.rdf4j.repository.sail.SailRepository(memoryStore);
        clearAllData();
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
        repository.initialize();
        return repository.getConnection();
    }
    @Override
    public void shutDown() {
        repository.shutDown();
    }
}
