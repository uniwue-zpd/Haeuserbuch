package de.uniwue.dachs.haeuserbuch_backend.specification;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonJob;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOrigin;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonReligion;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
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

    public static Specification<Person> hasJob(String job) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonJob> jobJoin = root.join("job", JoinType.LEFT);
            Join<Person, Job> jobCategoryJoin = jobJoin.join("jobCategory", JoinType.LEFT);
            String pattern = "%" + job.strip().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(jobJoin.get("originalText")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(jobCategoryJoin.get("name")), pattern)
            );
        };
    }

    public static Specification<Person> hasJobId(Long jobId) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonJob> jobJoin = root.join("job", JoinType.LEFT);
            return criteriaBuilder.equal(jobJoin.join("jobCategory").get("id"), jobId);
        };
    }

    public static Specification<Person> hasAssociatedBuilding(String building) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, Building> buildingJoin = root.join("associatedBuilding", JoinType.LEFT);
            String pattern = "%" + building.strip().toLowerCase().replace("/", "%") + "%";
            return criteriaBuilder.like(criteriaBuilder.lower(buildingJoin.get("districtPropertyNumber")), pattern);
        };
    }

    public static Specification<Person> hasAssociatedBuildingId(Long buildingId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("associatedBuilding").get("id"), buildingId);
    }

    public static Specification<Person> isCitizen(Boolean isCitizen) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isCitizen"), isCitizen);
    }

    public static Specification<Person> hasPlaceOfOrigin(String placeOfOrigin) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonOrigin> personOriginJoin = root.join("origin", JoinType.LEFT);
            Join<Person, Place> placeJoin = personOriginJoin.join("places", JoinType.LEFT);
            String pattern = "%" + placeOfOrigin.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(personOriginJoin.get("originalText")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(placeJoin.get("realName")), pattern)
            );
        };
    }

    public static Specification<Person> hasPlaceOfOriginId(Long placeId) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonOrigin> originJoin = root.join("origin", JoinType.LEFT);
            Join<Person, Place> placeJoin = originJoin.join("places");
            return criteriaBuilder.equal(placeJoin.get("id"), placeId);
        };
    }

    public static Specification<Person> hasOriginCertainty(PlaceCertainty certainty) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonOrigin> originJoin = root.join("origin", JoinType.LEFT);
            return criteriaBuilder.equal(originJoin.get("certainty"), certainty);
        };
    }

    public static Specification<Person> hasReligion(String religion) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonReligion> personReligionJoin = root.join("religion", JoinType.LEFT);
            Join<Person, Religion> religionJoin = personReligionJoin.join("religionCategory", JoinType.LEFT);
            String pattern = "%" + religion.strip().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(personReligionJoin.get("originalText")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(religionJoin.get("name")), pattern)
            );
        };
    }

    public static Specification<Person> hasReligionId(Long religionId) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, PersonReligion> personReligionJoin = root.join("religion", JoinType.LEFT);
            return criteriaBuilder.equal(personReligionJoin.join("religionCategory").get("id"), religionId);
        };
    }

    public static Specification<Person> hasWeapon(String weapon) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, Weaponry> personWeaponryJoin = root.join("weapons", JoinType.LEFT);
            Join<Person, Weapon> weaponJoin = personWeaponryJoin.join("weapon", JoinType.LEFT);
            String pattern = "%" + weapon.strip().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(personWeaponryJoin.get("originalText")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(weaponJoin.get("name")), pattern)
            );
        };
    }

    public static Specification<Person> hasWeaponId(Long weaponId) {
        return (root, query, criteriaBuilder) -> {
            Join<Person, Weaponry> personWeaponryJoin = root.join("weapons", JoinType.LEFT);
            return criteriaBuilder.equal(personWeaponryJoin.join("weaponCategory").get("id"), weaponId);
        };
    }
}
