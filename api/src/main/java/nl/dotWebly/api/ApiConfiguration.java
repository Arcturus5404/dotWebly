package nl.dotWebly.api;

import nl.dotWebly.api.converter.RdfCsvConverter;
import nl.dotWebly.api.converter.RdfHtmlConverter;
import nl.dotWebly.api.converter.RdfRioMessageConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Configuration
@EnableWebMvc
@PropertySource({
        "settings.properties",
        "repository.sail.properties",
        "repository.configuratie.properties",
        "repository.sparql.properties",
        "repository.virtuoso.properties"})
public class ApiConfiguration extends WebMvcConfigurerAdapter {

    private static HashMap<String, HttpMessageConverter<?>> converters;

    static {
        converters = new HashMap<>();
        //TODO: html, default xml rules, sparql, json rules, txt, csv, xlsx, docx, pdf, xmi, graphml, yed
        //see: https://github.com/architolk/Linked-Data-Theatre/blob/master/docs/Content-negotiation.md
        converters.put("html", new RdfHtmlConverter());
        converters.put("json", new RdfRioMessageConverter(RDFFormat.JSONLD));
        converters.put("jsonld", new RdfRioMessageConverter(RDFFormat.JSONLD));
        converters.put("xml", new RdfRioMessageConverter(RDFFormat.RDFXML));
        converters.put("rdf", new RdfRioMessageConverter(RDFFormat.RDFXML));
        converters.put("ttl", new RdfRioMessageConverter(RDFFormat.TURTLE));
        converters.put("rdf-json", new RdfRioMessageConverter(RDFFormat.RDFJSON));
        converters.put("n-triple", new RdfRioMessageConverter(RDFFormat.NTRIPLES));
        converters.put("csv", new RdfCsvConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.valueOf(RDFFormat.JSONLD.getDefaultMIMEType()))
                .parameterName("format")
                .favorPathExtension(true)
                .favorParameter(true)
                .ignoreUnknownPathExtensions(false)
                .ignoreAcceptHeader(false)
                .useJaf(true);

        for (String extension : converters.keySet()) {
            converters.get(extension).getSupportedMediaTypes().forEach(m -> configurer.mediaType(extension, m));
        }

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.addAll(ApiConfiguration.converters.values());

        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
