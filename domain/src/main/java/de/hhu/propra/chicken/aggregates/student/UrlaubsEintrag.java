package de.hhu.propra.chicken.aggregates.student;

import de.hhu.propra.chicken.stereotype.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;

record UrlaubsEintrag(LocalDateTime start, LocalDateTime ende) {

}
