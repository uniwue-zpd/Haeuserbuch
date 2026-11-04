package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A source attesting a building name, together with the relevant excerpt.
 * The page reference (e.g. "Heffner 134: ...") is part of the excerpt text.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingNameSource {
    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;

    @Column(columnDefinition = "TEXT")
    private String excerpt;
}
