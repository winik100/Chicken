package de.hhu.propra.chicken.aggregates.klausur;

import de.hhu.propra.chicken.stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@AggregateRoot
public class Klausur {

    Long id;
    LsfId lsfId;
    String name;
    LocalDateTime start;
    LocalDateTime ende;
    KlausurTyp typ = KlausurTyp.ONLINE;

    private final static LocalTime PRAKTIKUMSSTART = LocalTime.of(9, 30);
    private final static LocalTime ENDE = LocalTime.of(13, 30);



    public Klausur(LsfId lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        this.lsfId = lsfId;
        this.name = name;
        this.start = start;
        this.ende = ende;
        if (typ.equals("praesenz")) {
            this.typ = KlausurTyp.PRAESENZ;
        }
    }

    public LocalDateTime startFreistellungBerechnen() {
        LocalDateTime freistellungsBeginn = start;
        LocalTime praktikumsBeginn = LocalTime.of(9, 30);
        if(typ.equals(KlausurTyp.PRAESENZ)) {
            freistellungsBeginn = freistellungsBeginn.minusHours(2L);
        }
        else {
            freistellungsBeginn = freistellungsBeginn.minusMinutes(30L);
        }
        if(freistellungsBeginn.isBefore(LocalDateTime.of(start.toLocalDate(), praktikumsBeginn))) {
            freistellungsBeginn = LocalDateTime.of(start.toLocalDate(), praktikumsBeginn);
        }
        return freistellungsBeginn;
    }

    public LocalDateTime endeFreistellungBerechnen() {
        LocalDateTime freistellungsEnde = ende;
        LocalTime praktikumsEnde = LocalTime.of(13, 30);
        if(typ.equals(KlausurTyp.PRAESENZ)) {
            freistellungsEnde = freistellungsEnde.plusHours(2L);
        }
        if(freistellungsEnde.isAfter(LocalDateTime.of(ende.toLocalDate(), praktikumsEnde))) {
            freistellungsEnde = LocalDateTime.of(ende.toLocalDate(), praktikumsEnde);
        }
        return freistellungsEnde;
    }



    Long dauer() {
        return Duration.between(start, ende).toMinutes();
    }

    public LsfId getLsfId() {
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

    public LocalDateTime getEnde() {
        return ende;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTyp() {
        if(typ.equals(KlausurTyp.ONLINE)) {
            return "online";
        }
        return "praesenz";
    }
}








