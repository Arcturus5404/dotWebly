package nl.dotWebly.api.converter.office;

import nl.dotWebly.api.converter.WriteOnlyRdfConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfExcelOpenXmlConverter extends RdfExcelConverterBase {

    public RdfExcelOpenXmlConverter() {
        super(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=data.xlsx");
        createWorkbook(statements, () -> new XSSFWorkbook()).write(httpOutputMessage.getBody());
    }
}
