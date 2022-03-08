package repositories;

import aggregates.klausur.Klausur;

public interface KlausurRepository {
    Klausur klausurMitId(Long id);
    void save(Klausur klausur);
}
