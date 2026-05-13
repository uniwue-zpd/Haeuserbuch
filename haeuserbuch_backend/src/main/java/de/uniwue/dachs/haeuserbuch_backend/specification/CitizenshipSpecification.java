package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import org.springframework.data.jpa.domain.Specification;

public class CitizenshipSpecification {
    public static Specification<Citizenship> hasRefNumber(String refNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("refNumber")), "%" + refNumber.toLowerCase() + "%");
    }

    public static Specification<Citizenship> hasSignature(String signature) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("signature")), "%" + signature.toLowerCase() + "%");
    }

    public static Specification<Citizenship> hasNaturalizedPerson(String naturalizedPerson) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.join("person").get("fullName")), "%" + naturalizedPerson.toLowerCase() + "%");
    }

    public static Specification<Citizenship> hasDateNaturalization(String dateNaturalization) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("dateNaturalization")), "%" + dateNaturalization.toLowerCase() + "%");
    }

    public static Specification<Citizenship> hasPrimarySource(String primarySource) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.join("primarySource").get("title")), "%" + primarySource.toLowerCase() + "%");
    }

    public static Specification<Citizenship> hasSecondarySource(String secondarySource) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.join("secondarySource").get("title")), "%" + secondarySource.toLowerCase() + "%");
    }
}
