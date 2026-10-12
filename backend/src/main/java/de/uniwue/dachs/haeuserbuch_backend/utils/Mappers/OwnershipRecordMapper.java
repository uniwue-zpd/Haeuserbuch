package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.XML.XmlUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Maps ownership record XML to entities. The XML is expected to be valid against
 * {@code xsd/ownership_record.xsd}, so no defensive parsing is done here.
 */
@Component
public class OwnershipRecordMapper {

    /**
     * A building name from the entry with the ID of its source, if given.
     * @param name the building name
     * @param sourceId the ID of the source, or null if no source was given
     */
    public record BuildingNameInput(String name, Long sourceId) {}

    private final XmlUtils xmlUtils;
    private final BuildingRepository buildingRepository;

    public OwnershipRecordMapper(XmlUtils xmlUtils, BuildingRepository buildingRepository) {
        this.xmlUtils = xmlUtils;
        this.buildingRepository = buildingRepository;
    }

    public OwnershipRecord toEntity(String xml) {
        Document document = xmlUtils.parse(xml);
        Element recordElement = document.getDocumentElement();

        OwnershipRecord record = new OwnershipRecord();
        record.setSignature(xmlUtils.getText(recordElement, "signatur"));
        record.setEntryText(xmlUtils.extractText(recordElement));

        Element haus = xmlUtils.getChildElement(recordElement, "haus");
        if (haus != null) record.setSum(xmlUtils.getLong(haus, "summe"));

        for (Element element : districtPropertyNumberElements(recordElement)) {
            String number = xmlUtils.extractText(element);
            // searchBuildings matches substrings, so only the exact district property number is kept
            record.getBuildings().add(buildingRepository.searchBuildings(number).stream()
                    .filter(building -> number.equalsIgnoreCase(building.getDistrictPropertyNumber()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No building found with district property number '" + number + "'")));
        }

        return record;
    }

    /**
     * Extracts the district property numbers referenced within the {@code <haus>} element.
     * @param xml the ownership record XML
     * @return the district property numbers in document order
     */
    public List<String> extractDistrictPropertyNumbers(String xml) {
        return districtPropertyNumberElements(xmlUtils.parse(xml).getDocumentElement()).stream()
                .map(xmlUtils::extractText)
                .toList();
    }

    /**
     * Extracts the building names within the {@code <haus>} element.
     * @param xml the ownership record XML
     * @return the building names with their source IDs in document order
     */
    public List<BuildingNameInput> extractBuildingNames(String xml) {
        Element haus = xmlUtils.getChildElement(xmlUtils.parse(xml).getDocumentElement(), "haus");
        if (haus == null) return List.of();
        Element names = xmlUtils.getChildElement(haus, "namen");
        if (names == null) return List.of();
        return xmlUtils.getChildElements(names, "name").stream()
                .map(name -> new BuildingNameInput(
                        xmlUtils.extractText(name),
                        name.hasAttribute("quelleId")
                                ? Long.valueOf(name.getAttribute("quelleId").trim())
                                : null
                ))
                .toList();
    }

    /**
     * Enriches the XML with the entity attributes of the record and the IDs of the referenced buildings.
     * @param xml the ownership record XML
     * @param record the saved ownership record with its buildings
     * @return the enriched XML
     */
    public String enrichXml(String xml, OwnershipRecord record) {
        // Set BaseEntity attributes
        Document document = xmlUtils.parse(xml);
        Element recordElement = document.getDocumentElement();
        xmlUtils.enrichElement(recordElement, record);

        // Set the IDs of the referenced buildings
        for (Element number : districtPropertyNumberElements(recordElement)) {
            String text = xmlUtils.extractText(number);
            record.getBuildings().stream()
                    .filter(building -> text.equalsIgnoreCase(building.getDistrictPropertyNumber()))
                    .findFirst()
                    .ifPresent(building -> xmlUtils.setAttribute(number, "gebaeudeId", building.getId()));
        }

        return xmlUtils.serialize(document);
    }

    // The <distriktBesitzNummer> elements of <haus>; the wrapper is required by the XSD whenever <haus> exists.
    private List<Element> districtPropertyNumberElements(Element recordElement) {
        Element haus = xmlUtils.getChildElement(recordElement, "haus");
        if (haus == null) return List.of();
        Element numbers = xmlUtils.getChildElement(haus, "distriktBesitzNummern");
        return xmlUtils.getChildElements(numbers, "distriktBesitzNummer");
    }
}
