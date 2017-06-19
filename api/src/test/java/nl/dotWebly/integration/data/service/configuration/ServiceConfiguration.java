package nl.dotWebly.integration.data.service.configuration;

import nl.dotWebly.data.client.impl.ConfigurationTripleStoreClient;
import nl.dotWebly.data.repository.impl.ConfigurationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@Configuration
public class ServiceConfiguration {

    @Bean
    public ConfigurationRepository getConfigurationRepository(ResourceLoader loader) throws IOException {
        return new ConfigurationRepository(loader, "http://example.org", "testconfig" );
    }

    @Bean
    public ConfigurationTripleStoreClient getConfigurationTripleStoreClient() {
        return new ConfigurationTripleStoreClient();
    }
}
