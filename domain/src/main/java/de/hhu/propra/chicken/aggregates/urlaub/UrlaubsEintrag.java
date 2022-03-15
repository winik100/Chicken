package de.hhu.propra.chicken.aggregates.urlaub;

import de.hhu.propra.chicken.stereotype.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;

@AggregateRoot
public record UrlaubsEintrag (Long id, LocalDateTime start, LocalDateTime ende) {

}
