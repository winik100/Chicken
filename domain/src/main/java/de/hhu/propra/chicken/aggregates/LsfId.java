package de.hhu.propra.chicken.aggregates;

import java.util.Objects;

class LsfId {

    Long id;





    public LsfId(Long id) {
        this.id = id;
    }

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
