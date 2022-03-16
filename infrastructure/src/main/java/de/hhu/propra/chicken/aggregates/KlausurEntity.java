package de.hhu.propra.chicken.aggregates;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("klausur")
public record KlausurEntity(@Id Long id, Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
}
