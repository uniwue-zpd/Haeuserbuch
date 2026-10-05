package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import de.uniwue.dachs.haeuserbuch_backend.utils.XML.XmlUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class OwnershipRecordMapper {

    private final XmlUtils xmlUtils;

    public OwnershipRecordMapper(XmlUtils xmlUtils) {
        this.xmlUtils = xmlUtils;
    }

    public OwnershipRecord toEntity(String xml) {
        Document document = xmlUtils.parse(xml);
        Element recordElement = document.getDocumentElement();

        OwnershipRecord record = new OwnershipRecord();
        record.setSignature(xmlUtils.getText(recordElement, "signatur"));
        record.setEntryText(xmlUtils.extractText(recordElement));

        return record;
    }

    public String enrichXml(String xml, OwnershipRecord record) {
        Document document = xmlUtils.parse(xml);
        Element recordElement = document.getDocumentElement();
        xmlUtils.enrichElement(recordElement, record);
        return xmlUtils.serialize(document);
    }
}
