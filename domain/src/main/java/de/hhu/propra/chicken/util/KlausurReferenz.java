package de.hhu.propra.chicken.util;

import org.springframework.data.relational.core.mapping.Table;

@Table("student_belegt_klausur")
public record KlausurReferenz(Long klausur_id) {
}
