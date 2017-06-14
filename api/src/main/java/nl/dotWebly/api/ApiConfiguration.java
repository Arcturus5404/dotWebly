package nl.dotWebly.api;

import nl.dotWebly.api.converter.RdfMessageConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.*;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Configuration
@EnableWebMvc
@PropertySource({"settings.properties", "repository.sail.properties", "repository.virtuoso.properties"})
public class ApiConfiguration extends WebMvcConfigurerAdapter {

    private static HashMap<String, RdfMessageConverter> converters;
    static {
        converters = new HashMap<>();
        converters.put("json-ld", new RdfMessageConverter(RDFFormat.JSONLD));
        converters.put("xml", new RdfMessageConverter(RDFFormat.RDFXML));
        converters.put("ttl", new RdfMessageConverter(RDFFormat.TURTLE));
        converters.put("rdf-json", new RdfMessageConverter(RDFFormat.RDFJSON));
        converters.put("n-triple", new RdfMessageConverter(RDFFormat.NTRIPLES));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.valueOf(RDFFormat.JSONLD.getDefaultMIMEType()))
            .parameterName("format")
            .favorParameter(false)
            .favorPathExtension(true)
            .ignoreUnknownPathExtensions(false)
            .ignoreAcceptHeader(true)
            .useJaf(true);

        for (String extension : converters.keySet()) {
            converters.get(extension).getSupportedMediaTypes().forEach(m -> configurer.mediaType(extension, m));
        }
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.addAll(this.converters.values());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
