package klausur;

import stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@AggregateRoot
public record Klausur (long id, LsfId lsfId, String name, LocalDateTime start, LocalDateTime ende, KlausurTyp typ) {

    Long dauer() {
        return Duration.between(start, ende).toMinutes();
    }
}








