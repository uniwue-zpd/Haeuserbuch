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

    public static Specification<Building> hasQuarterId(Long quarterId) {
        return (root, query, cb) ->
                cb.equal(root.get("quarter").get("id"), quarterId);
    }

    public static Specification<Building> hasStreetId(Long streetId) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<Object, Object> addressJoin = root.joinSet("addresses", JoinType.LEFT);
            return cb.equal(addressJoin.get("street").get("id"), streetId);
        };
    }

    public static Specification<Building> hasSourceId(Long sourceId) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<Object, Object> sourcesJoin = root.joinSet("sources", JoinType.LEFT);
            Join<Object, Object> literatureJoin = root.joinSet("literature", JoinType.LEFT);
            return cb.or(
                    cb.equal(sourcesJoin.get("id"), sourceId),
                    cb.equal(literatureJoin.get("id"), sourceId)
            );
        };
    }

    public static Specification<Building> isGeoreferenced(boolean georeferenced) {
        return (root, query, cb) -> georeferenced
                ? cb.isNotNull(root.get("coordinates"))
                : cb.isNull(root.get("coordinates"));
    }

    /**
     * Case-insensitive substring search over numbers, names, object, part type, district, quarter and addresses.
     */
    public static Specification<Building> matchesText(String text) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            String pattern = "%" + text.trim().toLowerCase() + "%";
            Join<Object, Object> districtJoin = root.join("district", JoinType.LEFT);
            Join<Object, Object> quarterJoin = root.join("quarter", JoinType.LEFT);
            Join<Object, Object> namesJoin = root.joinList("names", JoinType.LEFT);
            Join<Object, Object> addressJoin = root.joinSet("addresses", JoinType.LEFT);
            Join<Object, Object> streetJoin = addressJoin.join("street", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("districtPropertyNumber")), pattern),
                    cb.like(cb.lower(root.get("propertyNumber")), pattern),
                    cb.like(cb.lower(root.get("object")), pattern),
                    cb.like(cb.lower(root.get("partType")), pattern),
                    cb.like(cb.lower(namesJoin.get("name")), pattern),
                    cb.like(cb.lower(districtJoin.get("name")), pattern),
                    cb.like(cb.lower(quarterJoin.get("name")), pattern),
                    cb.like(cb.lower(streetJoin.get("name")), pattern),
                    cb.like(cb.lower(addressJoin.get("houseNumber")), pattern),
                    cb.like(cb.lower(cb.concat(cb.concat(streetJoin.get("name"), " "), addressJoin.get("houseNumber"))), pattern)
            );
        };
    }
}
