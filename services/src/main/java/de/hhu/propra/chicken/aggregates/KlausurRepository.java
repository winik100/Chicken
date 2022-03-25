package de.hhu.propra.chicken.aggregates;

import java.util.Set;

public interface KlausurRepository {

    Klausur klausurMitLsfId(Long id);

    void save(Klausur klausur);

    Set<Klausur> klausurenMitReferenzen(Set<Long> referenzen);

    Set<Klausur> alle();
}
