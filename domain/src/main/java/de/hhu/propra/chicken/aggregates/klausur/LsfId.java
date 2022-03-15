package de.hhu.propra.chicken.aggregates.klausur;

import java.util.Objects;

public class LsfId {

    Long id;

    public LsfId(Long id) {
        // TODO: 07.03.2022 Validierung
        this.id = id;
    }

/*    boolean istGueltig(int id) {
        // TODO: 07.03.2022 Validierung
        return false;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LsfId lsfId = (LsfId) o;
        return id.equals(lsfId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }
}
