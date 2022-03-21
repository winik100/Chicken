package de.hhu.propra.chicken.aggregates;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("klausur")
public class KlausurEntity {

    @Id Long id;
    @Column("lsf_id")Long lsfId;
    String name;
    LocalDateTime start;
    LocalDateTime ende;
    String typ;

    public KlausurEntity(Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        this.lsfId = lsfId;
        this.name = name;
        this.start = start;
        this.ende = ende;
        this.typ = typ;
    }

    public Long getId() {
        return id;
    }

    public Long getLsfId() {
        return lsfId;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnde() {
        return ende;
    }

    public String getTyp() {
        return typ;
    }
}
