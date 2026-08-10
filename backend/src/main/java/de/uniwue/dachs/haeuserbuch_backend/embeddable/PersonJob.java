package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PersonJob {
    private String originalText;

    @ManyToOne
    @JoinColumn(name = "job_category_id")
    private Job jobCategory;
}
