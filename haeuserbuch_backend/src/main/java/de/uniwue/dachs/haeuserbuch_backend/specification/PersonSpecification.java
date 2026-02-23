package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOrigin;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

/**
 * Specifications for filtering {@link Person} entities based on various attributes.
 */
public class PersonSpecification {
    public static Specification<Person> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, String> altNamesJoin = root.join("altNames", JoinType.LEFT);
            String pattern = "%" + name.strip().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(altNamesJoin), pattern)
            );
        };
    }

    public static Specification<Person> hasSex(String sex) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("sex")), "%" + sex.toLowerCase() + "%");
    }

    public static Specification<Person> hasOccupation(String occupation) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("occupation")), "%" + occupation.toLowerCase() + "%");
    }

    public static Specification<Person> hasAssociatedBuildingId(Long buildingId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("associatedBuilding").get("id"), buildingId);
    }

    public static Specification<Person> isCitizen(Boolean isCitizen) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isCitizen"), isCitizen);
    }

    public static Specification<Person> hasPlaceOfOriginId(Long placeId) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonOrigin> originJoin = root.join("origin");
            Join<Person, Place> placeJoin = originJoin.join("places");
            return criteriaBuilder.equal(placeJoin.get("id"), placeId);
        };
    }

    public static Specification<Person> hasOriginCertainty(PlaceCertainty certainty) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonOrigin> originJoin = root.join("origin");
            return criteriaBuilder.equal(originJoin.get("certainty"), certainty);
        };
    }
}
