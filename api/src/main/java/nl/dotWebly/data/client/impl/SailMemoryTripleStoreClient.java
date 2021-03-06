package nl.dotWebly.data.client.impl;

import nl.dotWebly.data.repository.impl.SailMemoryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
@Primary
public class SailMemoryTripleStoreClient extends TripleStoreClientImpl<SailMemoryRepository> {
    public SailMemoryTripleStoreClient() {
        super("Memory");
    }
}
