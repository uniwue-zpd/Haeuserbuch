package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import de.uniwue.dachs.haeuserbuch_backend.model.Religion;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PersonReligion {
    private String originalText;

    @ManyToOne
    @JoinColumn(name = "religion_category_id")
    private Religion religionCategory;
}
