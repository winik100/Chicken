package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;

@AggregateRoot
public class Klausur {

    Long id;
    LsfId lsfId;
    String name;
    LocalDateTime start;
    LocalDateTime ende;
    KlausurTyp typ = KlausurTyp.ONLINE;

    public Klausur(Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        this.lsfId = new LsfId(lsfId);
        this.name = name;
        this.start = start;
        this.ende = ende;
        if (typ.equals("praesenz")) {
            this.typ = KlausurTyp.PRAESENZ;
        }
    }

    public Klausur(Long id, Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        this.id = id;
        this.lsfId = new LsfId(lsfId);
        this.name = name;
        this.start = start;
        this.ende = ende;
        if (typ.equals("praesenz")) {
            this.typ = KlausurTyp.PRAESENZ;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLsfId() {
        return lsfId.getId();
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnde() {
        return ende;
    }

    public String getTyp() {
        if(typ.equals(KlausurTyp.ONLINE)) {
            return "online";
        }
        return "praesenz";
    }

    public LocalDateTime startFreistellungBerechnen() {
        LocalDateTime freistellungsBeginn = start;
        if (typ.equals(KlausurTyp.PRAESENZ)) {
            freistellungsBeginn = freistellungsBeginn.minusHours(2L);
        } else {
            freistellungsBeginn = freistellungsBeginn.minusMinutes(30L);
        }
        return freistellungsBeginn;
    }

    public LocalDateTime endeFreistellungBerechnen() {
        LocalDateTime freistellungsEnde = ende;
        if (typ.equals(KlausurTyp.PRAESENZ)) {
            freistellungsEnde = freistellungsEnde.plusHours(2L);
        }
        return freistellungsEnde;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Klausur klausur = (Klausur) o;
        return lsfId.equals(klausur.lsfId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}








