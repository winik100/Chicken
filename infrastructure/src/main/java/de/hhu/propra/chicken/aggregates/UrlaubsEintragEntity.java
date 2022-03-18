package de.hhu.propra.chicken.aggregates;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("urlaubs_eintrag")
public record UrlaubsEintragEntity(@Id Long id, LocalDateTime start, LocalDateTime ende) {}
