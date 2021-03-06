package nl.dotWebly.data.client.impl;

import nl.dotWebly.data.repository.impl.VirtuosoRepository;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class VirtuosoTripleStoreClient extends TripleStoreClientImpl<VirtuosoRepository> {
    public VirtuosoTripleStoreClient() {
        super("virtuoso");
    }
}
