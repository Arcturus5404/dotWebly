package nl.dotWebly.integration.data.client.configuration;

import nl.dotWebly.data.client.TripleStoreClient;
import nl.dotWebly.data.client.impl.TripleStoreClientImpl;
import nl.dotWebly.data.repository.TripleStoreRepository;
import nl.dotWebly.data.repository.impl.SailMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Testcase configuration with default beans
 *
 * Created by Rick Fleuren on 6/9/2017.
 */
@Configuration
public class SailMemoryTestConfiguration {

    @Bean
    public TripleStoreRepository getTripleStoreRepository() {
        return new SailMemoryRepository();
    }
    @Bean
    public TripleStoreClient getTripleStoreClient() {
        return new TripleStoreClientImpl();
    }
}
