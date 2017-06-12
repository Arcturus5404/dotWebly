package nl.dotWebly.api;

import nl.dotWebly.api.converter.RdfMessageConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Configuration
@EnableWebMvc
public class ApiConfiguration extends WebMvcConfigurerAdapter {

    private RdfMessageConverter ttlConverter;
    private RdfMessageConverter rdfXmlConverter;
    private RdfMessageConverter jsonLdConverter;
    private RdfMessageConverter rdfJsonConverter;

    public ApiConfiguration() {
        this.jsonLdConverter = new RdfMessageConverter(RDFFormat.JSONLD);
        this.rdfXmlConverter = new RdfMessageConverter(RDFFormat.RDFXML);
        this.ttlConverter = new RdfMessageConverter(RDFFormat.TURTLE);
        this.rdfJsonConverter = new RdfMessageConverter(RDFFormat.RDFJSON);
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


        rdfXmlConverter.getSupportedMediaTypes().forEach(m -> configurer.mediaType("xml", m));
        ttlConverter.getSupportedMediaTypes().forEach(m -> configurer.mediaType("ttl", m));
        jsonLdConverter.getSupportedMediaTypes().forEach(m -> configurer.mediaType("json", m));
        rdfJsonConverter.getSupportedMediaTypes().forEach(m -> configurer.mediaType("rdf-json", m));
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(rdfXmlConverter);
        converters.add(ttlConverter);
        converters.add(jsonLdConverter);
        converters.add(rdfJsonConverter);
    }

}
