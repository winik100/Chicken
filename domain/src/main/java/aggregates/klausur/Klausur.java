package aggregates.klausur;

import stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@AggregateRoot
public class Klausur {

    int lsfId;
    String name;
    LocalDateTime start;
    LocalDateTime ende;
    KlausurTyp typ = KlausurTyp.ONLINE;

    public Klausur(int lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        this.lsfId = lsfId;
        this.name = name;
        this.start = start;
        this.ende = ende;
        if (typ.equals("praesenz")) {
            this.typ = KlausurTyp.PRAESENZ;
        }
    }

    Long dauer() {
        return Duration.between(start, ende).toMinutes();
    }

    public int getLsfId() {
        return lsfId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klausur klausur = (Klausur) o;
        return lsfId == klausur.lsfId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lsfId);
    }
}








