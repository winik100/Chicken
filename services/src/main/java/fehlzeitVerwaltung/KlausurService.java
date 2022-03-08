package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import repositories.KlausurRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class KlausurService {
    private final KlausurRepository repo;

    public KlausurService(KlausurRepository repo) {
        this.repo = repo;
    }

    void klausurHinzufuegen(int lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        if (klausur == null) {
            klausur = new Klausur(lsfId, name, start, ende, typ);
            repo.save(klausur);
        }
    }

    Optional<Klausur> findeKlausur(int lsfId) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        return Optional.ofNullable(klausur);
    }

}
