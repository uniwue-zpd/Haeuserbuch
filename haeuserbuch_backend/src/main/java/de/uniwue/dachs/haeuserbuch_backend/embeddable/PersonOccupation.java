package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import de.uniwue.dachs.haeuserbuch_backend.model.Occupation;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PersonOccupation {
    private String originalText;

    @ManyToOne
    @JoinColumn(name = "occupation_category_id")
    private Occupation occupationCategory;
}
