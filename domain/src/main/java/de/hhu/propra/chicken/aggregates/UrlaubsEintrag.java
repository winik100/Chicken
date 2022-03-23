package de.hhu.propra.chicken.aggregates;

import java.time.Duration;
import java.time.LocalDateTime;

record UrlaubsEintrag(LocalDateTime start, LocalDateTime ende) {
}
