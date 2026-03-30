package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Embeddable
@Getter
@Setter
public class PersonWeapons {
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "person_weapon_names", joinColumns = @JoinColumn(name = "person_id"))
    private Set<String> originalNames = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "person_weapons",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "weapon_id")
    )
    private Set<Weapon> weaponCategories = new HashSet<>();
}
