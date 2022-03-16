package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Klausur;
import de.hhu.propra.chicken.aggregates.LsfId;
import de.hhu.propra.chicken.util.KlausurReferenz;

import java.util.Set;

public interface KlausurRepository {
    Klausur klausurMitLsfId(LsfId id);

    void save(Klausur klausur);

    Set<Klausur> klausurenMitReferenzen(Set<KlausurReferenz> referenzen);
}
