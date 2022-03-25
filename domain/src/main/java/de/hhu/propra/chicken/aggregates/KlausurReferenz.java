package de.hhu.propra.chicken.aggregates;

import org.springframework.data.relational.core.mapping.Table;

@Table("student_belegt_klausur")
record KlausurReferenz(Long klausur_id) {
}
