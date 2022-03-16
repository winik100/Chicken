package de.hhu.propra.chicken.aggregates;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record KlausurEntity(@Id Long id, Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
}
