package nl.dotWebly.api.converter.office;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfExcelConverter extends RdfExcelConverterBase {
    public RdfExcelConverter() {
        super(MediaType.valueOf("application/vnd.ms-excel"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=data.xls");
        createWorkbook(statements, () -> new HSSFWorkbook()).write(httpOutputMessage.getBody());
    }
}
