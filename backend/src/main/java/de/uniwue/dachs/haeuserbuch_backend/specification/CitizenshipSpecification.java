package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonJob;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOrigin;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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

    /** Matches normalized names (full/first/last name) as well as original spellings (altNames) */
    private static Predicate nameMatches(CriteriaBuilder cb, Join<?, Person> personJoin, String value) {
        Join<Person, String> altNamesJoin = personJoin.join("altNames", JoinType.LEFT);
        String pattern = "%" + value.strip().toLowerCase() + "%";
        return cb.or(
                cb.like(cb.lower(personJoin.get("fullName")), pattern),
                cb.like(cb.lower(personJoin.get("firstName")), pattern),
                cb.like(cb.lower(personJoin.get("lastName")), pattern),
                cb.like(cb.lower(altNamesJoin), pattern)
        );
    }

    /** Matches the original job text as well as the normalized job category */
    private static Predicate jobMatches(CriteriaBuilder cb, Join<?, Person> personJoin, String value) {
        Join<Person, PersonJob> jobJoin = personJoin.join("job", JoinType.LEFT);
        Join<PersonJob, Job> jobCategoryJoin = jobJoin.join("jobCategory", JoinType.LEFT);
        String pattern = "%" + value.strip().toLowerCase() + "%";
        return cb.or(
                cb.like(cb.lower(jobJoin.get("originalText")), pattern),
                cb.like(cb.lower(jobCategoryJoin.get("name")), pattern)
        );
    }

    /** Matches the original origin text as well as the identified places */
    private static Predicate originMatches(CriteriaBuilder cb, Join<?, Person> personJoin, String value) {
        Join<Person, PersonOrigin> originJoin = personJoin.join("origin", JoinType.LEFT);
        Join<PersonOrigin, Place> placeJoin = originJoin.join("places", JoinType.LEFT);
        String pattern = "%" + value.strip().toLowerCase() + "%";
        return cb.or(
                cb.like(cb.lower(originJoin.get("originalText")), pattern),
                cb.like(cb.lower(placeJoin.get("realName")), pattern)
        );
    }

    public static Specification<Citizenship> hasNaturalizedPerson(String naturalizedPerson) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            return nameMatches(criteriaBuilder, root.join("person", JoinType.LEFT), naturalizedPerson);
        };
    }

    public static Specification<Citizenship> hasNaturalizedPersonJob(String job) {
        return (root, query, criteriaBuilder) ->
                jobMatches(criteriaBuilder, root.join("person", JoinType.LEFT), job);
    }

    public static Specification<Citizenship> hasNaturalizedPersonOrigin(String origin) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            return originMatches(criteriaBuilder, root.join("person", JoinType.LEFT), origin);
        };
    }

    public static Specification<Citizenship> hasNaturalizedPersonId(Long naturalizedPersonId) {
        return (root, query, criteriaBuilder) -> {
            Join<Citizenship, Person> personJoin = root.join("person", JoinType.LEFT);
            return criteriaBuilder.equal(personJoin.get("id"), naturalizedPersonId);
        };
    }

    public static Specification<Citizenship> hasGuarantor(String guarantor) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            return nameMatches(criteriaBuilder, root.joinSet("guarantors", JoinType.LEFT), guarantor);
        };
    }

    public static Specification<Citizenship> hasGuarantorJob(String job) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            return jobMatches(criteriaBuilder, root.joinSet("guarantors", JoinType.LEFT), job);
        };
    }

    public static Specification<Citizenship> hasGuarantorOrigin(String origin) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            return originMatches(criteriaBuilder, root.joinSet("guarantors", JoinType.LEFT), origin);
        };
    }

    public static Specification<Citizenship> hasGuarantorId(Long guarantorId) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true);
            Join<Citizenship, Person> guarantorJoin = root.joinSet("guarantors", JoinType.LEFT);
            return criteriaBuilder.equal(guarantorJoin.get("id"), guarantorId);
        };
    }

    public static Specification<Citizenship> hasYearNaturalization(Integer year) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("yearNaturalization"), year);
    }

    public static Specification<Citizenship> hasYearFrom(Integer yearFrom) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("yearNaturalization"), yearFrom);
    }

    public static Specification<Citizenship> hasYearTo(Integer yearTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("yearNaturalization"), yearTo);
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
