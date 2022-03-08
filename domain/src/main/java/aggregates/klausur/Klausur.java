package aggregates.klausur;

import stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDateTime;

@AggregateRoot
public record Klausur (Long id, LsfId lsfId, String name, LocalDateTime start, LocalDateTime ende, KlausurTyp typ) {

    Long dauer() {
        return Duration.between(start, ende).toMinutes();
    }
}








