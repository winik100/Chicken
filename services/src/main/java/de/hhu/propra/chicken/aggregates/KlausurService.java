package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.DomainService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
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
