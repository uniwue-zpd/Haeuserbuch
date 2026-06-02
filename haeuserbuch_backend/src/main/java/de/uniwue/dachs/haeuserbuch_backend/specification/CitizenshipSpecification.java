package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
        return (root, query, criteriaBuilder) -> {
            Join<Citizenship, Person> personJoin = root.join("person", JoinType.LEFT);
            String pattern = "%" + naturalizedPerson.strip().toLowerCase() + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(personJoin.get("fullName")), pattern);
        };
    }

    public static Specification<Citizenship> hasNaturalizedPersonId(Long naturalizedPersonId) {
        return (root, query, criteriaBuilder) -> {
            Join<Citizenship, Person> personJoin = root.join("person", JoinType.LEFT);
            return criteriaBuilder.equal(personJoin.get("id"), naturalizedPersonId);
        };
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
