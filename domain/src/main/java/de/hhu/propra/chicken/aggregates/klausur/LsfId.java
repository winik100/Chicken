package de.hhu.propra.chicken.aggregates.klausur;

import java.util.Objects;

class LsfId {

    int id;

    public LsfId(int id) {
        // TODO: 07.03.2022 Validierung
        this.id = id;
    }

    boolean istGueltig(int id) {
        // TODO: 07.03.2022 Validierung
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LsfId lsfId = (LsfId) o;
        return id == lsfId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
