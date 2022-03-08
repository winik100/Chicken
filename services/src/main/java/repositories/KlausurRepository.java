package repositories;

import aggregates.klausur.Klausur;

public interface KlausurRepository {
    Klausur klausurMitLsfId(int id);
    void save(Klausur klausur);
}
