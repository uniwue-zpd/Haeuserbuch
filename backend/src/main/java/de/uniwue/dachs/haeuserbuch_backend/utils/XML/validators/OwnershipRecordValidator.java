package de.uniwue.dachs.haeuserbuch_backend.utils.XML.validators;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class OwnershipRecordValidator {
    private final Schema schema;

    public OwnershipRecordValidator() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ClassPathResource schemaResource = new ClassPathResource("xsd/ownership_record.xsd");
            try (InputStream inputStream = schemaResource.getInputStream()) {
                this.schema = schemaFactory.newSchema(new StreamSource(inputStream));
            }
        } catch (SAXException | IOException e) {
                throw new IllegalStateException("Could not load XML schema", e);
            }
    }

    public List<String> validate(String xml) {
        List<String> errors = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    errors.add(format(e));
                }
                @Override
                public void error(SAXParseException e) {
                    errors.add(format(e));
                }
                @Override
                public void fatalError(SAXParseException e) {
                    errors.add(format(e));
                }
            });
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException | IOException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }

    private String format(SAXParseException e) {
        return "Line "
                + e.getLineNumber()
                + ", column " + e.getColumnNumber()
                + ": " + e.getMessage();
    }
}
