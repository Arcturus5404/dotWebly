package nl.dotWebly.api.converter;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/12/2017.
 */
public class RdfMessageConverter extends AbstractHttpMessageConverter<Model> {

    private RDFFormat format;

    public RdfMessageConverter(RDFFormat format) {
        super(format.getMIMETypes().stream()
                .filter(m -> !"application/xml".equals(m))
                .filter(m -> !"text/xml".equals(m))
                .map(m -> MediaType.valueOf(m)).toArray(MediaType[]::new));

        this.format = format;
    }
    @Override
    protected boolean supports(Class<?> aClass) {
        return Model.class.isAssignableFrom(aClass);
    }

    @Override
    protected Model readInternal(Class<? extends Model> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Read not supported");
    }

    @Override
    protected void writeInternal(Model model, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        Rio.write(model, httpOutputMessage.getBody(), format);
    }
}
