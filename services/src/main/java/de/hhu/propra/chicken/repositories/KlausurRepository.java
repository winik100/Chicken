package de.hhu.propra.chicken.repositories;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.klausur.LsfId;
import de.hhu.propra.chicken.aggregates.student.KlausurReferenz;

import java.util.Set;

public interface KlausurRepository {
    Klausur klausurMitLsfId(LsfId id);

    void save(Klausur klausur);

    Set<Klausur> klausurenMitReferenzen(Set<KlausurReferenz> referenzen);
}
