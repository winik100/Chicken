package de.hhu.propra.chicken.repositories;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;

public interface KlausurRepository {
    Klausur klausurMitLsfId(int id);

    void save(Klausur klausur);
}
