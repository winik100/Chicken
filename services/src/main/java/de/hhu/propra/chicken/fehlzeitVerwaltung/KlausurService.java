package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.klausur.LsfId;
import de.hhu.propra.chicken.repositories.KlausurRepository;
import de.hhu.propra.chicken.stereotypes.DomainService;

import java.time.LocalDateTime;
import java.util.Optional;

@DomainService
public class KlausurService {
    private final KlausurRepository repo;

    public KlausurService(KlausurRepository repo) {
        this.repo = repo;
    }

    void klausurHinzufuegen(LsfId lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        if (klausur == null) {
            klausur = new Klausur(lsfId, name, start, ende, typ);
            repo.save(klausur);
        }
    }

    Optional<Klausur> findeKlausur(LsfId lsfId) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        return Optional.ofNullable(klausur);
    }

}
