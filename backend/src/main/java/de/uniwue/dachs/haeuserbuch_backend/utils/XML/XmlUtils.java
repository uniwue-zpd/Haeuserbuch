package de.uniwue.dachs.haeuserbuch_backend.utils.XML;

import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.util.ArrayList;
import java.util.List;

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
     * Retrieves the direct child elements of an XML element with a specific tag name.
     * @param parent the parent XML element
     * @param tagName the name of the child elements to retrieve
     * @return the matching direct child elements in document order
     */
    public List<Element> getChildElements(Element parent, String tagName) {
        List<Element> children = new ArrayList<>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element && tagName.equals(element.getTagName())) {
                children.add(element);
            }
        }
        return children;
    }

    /**
     * Retrieves the first direct child element of an XML element with a specific tag name.
     * @param parent the parent XML element
     * @param tagName the name of the child element to retrieve
     * @return the first matching direct child element, or null if it does not exist
     */
    public Element getChildElement(Element parent, String tagName) {
        List<Element> children = getChildElements(parent, tagName);
        return children.isEmpty() ? null : children.getFirst();
    }

    /**
     * Retrieves the trimmed text content of a direct child element.
     * @param parent the parent XML element
     * @param tagName the name of the child element
     * @return the trimmed text content, or null if the element does not exist
     */
    public String getText(Element parent, String tagName) {
        Element element = getChildElement(parent, tagName);
        return element == null ? null : extractText(element);
    }

    /**
     * Retrieves the value of a direct child element as a Long.
     * @param parent the parent XML element
     * @param tagName the name of the child element
     * @return the value, or null if the element does not exist
     */
    public Long getLong(Element parent, String tagName) {
        String text = getText(parent, tagName);
        return text == null ? null : Long.valueOf(text);
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
