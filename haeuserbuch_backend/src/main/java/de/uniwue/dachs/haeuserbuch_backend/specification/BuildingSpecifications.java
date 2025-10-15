package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for filtering Building entities based on various criteria.
 */
public class BuildingSpecifications {
    public static Specification<Building> hasDistrictId(Long districtId) {
        return (root, query, cb) ->
                cb.equal(root.get("district").get("id"), districtId);
    }

    public static Specification<Building> hasDistrict(String districtName) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("district").get("name")), "%" + districtName.toLowerCase() + "%"
                );
    }

    public static Specification<Building> hasQuarterId(Long quarterId) {
        return (root, query, cb) ->
                cb.equal(root.get("quarter").get("id"), quarterId);
    }

    public static Specification<Building> hasQuarter(String quarterName) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("quarter").get("name")), "%" + quarterName.toLowerCase() + "%"
                );
    }

    public static Specification<Building> hasStreetId(Long streetId) {
        return (root, query, cb) ->
                cb.equal(root.get("currentStreet").get("id"), streetId);
    }

    public static Specification<Building> hasStreet(String streetName) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("currentStreet").get("name")), "%" + streetName.toLowerCase() + "%"
                );
    }
}
