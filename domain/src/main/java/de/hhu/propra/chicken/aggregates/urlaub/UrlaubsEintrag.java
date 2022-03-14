package de.hhu.propra.chicken.aggregates.urlaub;

import de.hhu.propra.chicken.stereotype.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;

@AggregateRoot
public record UrlaubsEintrag (LocalDateTime start, LocalDateTime ende) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrlaubsEintrag that = (UrlaubsEintrag) o;
        return start.equals(that.start) && ende.equals(that.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, ende);
    }
}
