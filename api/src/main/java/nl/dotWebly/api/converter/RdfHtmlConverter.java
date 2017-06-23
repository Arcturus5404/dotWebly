package nl.dotWebly.api.converter;

import j2html.tags.ContainerTag;
import j2html.tags.Tag;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static j2html.TagCreator.*;
import static org.springframework.http.MediaType.APPLICATION_XHTML_XML;
import static org.springframework.http.MediaType.TEXT_HTML;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
public class RdfHtmlConverter extends AbstractHttpMessageConverter<Model> {

    public RdfHtmlConverter() {
        super(TEXT_HTML, APPLICATION_XHTML_XML);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return Model.class.isAssignableFrom(aClass);
    }

    @Override
    protected Model readInternal(Class<? extends Model> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("This converter does not support parsing of html");
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        ContainerTag html = html(
                head(
                        link().withHref("https://unpkg.com/purecss@1.0.0/build/pure-min.css").withRel("stylesheet")
                ),
                body(
                        table(
                                thead(tr(th("Subject"), th("Predicate"), th("Object"))),
                                tbody(
                                    each(statements, s->
                                        tr(td(s.getSubject().toString()),
                                                td(s.getPredicate().toString()),
                                                td(s.getObject().toString()))
                                    ))
                        ).withClass("pure-table pure-table-horizontal pure-table-striped")
                )
        );

        try(OutputStreamWriter writer = new OutputStreamWriter(httpOutputMessage.getBody(), StandardCharsets.UTF_8)) {
            html.render(writer);
        }

    }
}
