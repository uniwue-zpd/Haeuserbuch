package de.uniwue.dachs.haeuserbuch_backend.utils.XML;

import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class XmlUtils {

    /**
     * Parses an XML string into a Document object.
     * @param xml the XML string to parse
     * @return the parsed Document object
     * @throws IllegalArgumentException if the XML is invalid
     */
    public Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid XML", e);
        }
    }

    /**
     * Sets an attribute on an XML element if the value is not null.
     * @param element the XML element to set the attribute on
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(Element element, String name, Object value) {
        if (value != null) {
            element.setAttribute(name, value.toString());
        }
    }

    /**
     * Retrieves the text content of a specific tag from an XML element.
     * @param parent the parent XML element
     * @param tagName the name of the tag to retrieve
     * @return the text content of the tag, or null if the tag does not exist
     */
    public String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return nodes.getLength() > 0
                ? nodes.item(0).getTextContent()
                : null;
    }

    /**
     * Extracts and trims the text content from an XML element.
     * @param element the XML element to extract text from
     * @return the trimmed text content of the element
     */
    public String extractText(Element element) {
        return element.getTextContent().trim();
    }

    /**
     * Serializes a Document object back into an XML string.
     * @param document the Document object to serialize
     * @return the serialized XML string
     * @throws IllegalArgumentException if serialization fails
     */
    public String serialize(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(writer)
            );
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalArgumentException("Could not serialize XML", e);
        }
    }

    /**
     * Enriches an XML element with attributes from a BaseEntity.
     * @param element the XML element to enrich
     * @param entity the BaseEntity containing the attributes to add
     */
    public void enrichElement(Element element, BaseEntity entity) {
        setAttribute(element, "id", entity.getId());
        setAttribute(element, "createdDate", entity.getCreatedDate().getTime());
        setAttribute(element, "createdBy", entity.getCreatedBy());
        setAttribute(element, "lastModifiedDate", entity.getLastModifiedDate().getTime());
        setAttribute(element, "lastModifiedBy", entity.getLastModifiedBy());
    }
}
