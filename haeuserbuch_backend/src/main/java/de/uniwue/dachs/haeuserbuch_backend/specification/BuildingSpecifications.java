package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

    public static Specification<Building> hasSourceId(Long sourceId) {
        return (root, query, cb) -> {
            Join<Object, Object> primarySourcesJoin = root.joinSet("primarySources", JoinType.LEFT);
            Join<Object, Object> secondarySourcesJoin = root.joinSet("secondarySources", JoinType.LEFT);
            return cb.or(
                    cb.equal(primarySourcesJoin.get("id"), sourceId),
                    cb.equal(secondarySourcesJoin.get("id"), sourceId)
            );
        };
    }

    public static Specification<Building> hasSourceName(String sourceName) {
        return (root, query, cb) -> {
            Join<Object, Object> primarySourcesJoin = root.joinSet("primarySources", JoinType.LEFT);
            Join<Object, Object> secondarySourcesJoin = root.joinSet("secondarySources", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(primarySourcesJoin.get("name")), "%" + sourceName.toLowerCase() + "%"),
                    cb.like(cb.lower(secondarySourcesJoin.get("name")), "%" + sourceName.toLowerCase() + "%")
            );
        };
    }
}
