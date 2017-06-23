package nl.dotWebly.api.converter.office;

import nl.dotWebly.api.converter.ResourceConverter;
import nl.dotWebly.api.converter.WriteOnlyRdfConverter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfWordConverter extends WriteOnlyRdfConverter implements ResourceConverter {

    private ResourceLoader loader;

    public RdfWordConverter() {
        super(MediaType.valueOf("application/msword"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        assert loader != null;

        httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=document.doc");
        Resource resource = loader.getResource("classpath:templates/template.doc");
        HWPFDocument document = new HWPFDocument(resource.getInputStream());
        addRdfData(document, statements);

        document.write(httpOutputMessage.getBody());
    }

    private void addRdfData(HWPFDocument document, Model statements) {
        Range range = document.getRange();

        statements.forEach(statement -> {
            String line = statement.getSubject().toString() + " " + statement.getPredicate().toString() + " " + statement.getObject() + "\r";
            CharacterRun charRun = range.insertAfter(line);
            charRun.setFontSize(18);
        });
    }

    @Override
    public void setResourceLoader(ResourceLoader loader) {
        this.loader = loader;
    }
}
