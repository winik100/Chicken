package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.DomainService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class KlausurService {
    private final KlausurRepository repo;
    private final AuditLog log = new AuditLog("auditLog.txt");

    public KlausurService(KlausurRepository repo) throws IOException {
        this.repo = repo;
    }

    public void klausurHinzufuegen(Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) throws IOException {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        if (klausur == null) {
            klausur = new Klausur(lsfId, name, start, ende, typ);
            log.eintragen("Klausur mit LsfId " + lsfId + " registriert.");
            repo.save(klausur);
        }
        else {
            log.eintragen("Klausur mit LsfId " + lsfId + " existiert bereits.");
        }
    }

    public Optional<Klausur> findeKlausur(Long lsfId) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        return Optional.ofNullable(klausur);
    }

    public Set<Klausur> findeKlausurenMitIds(Set<Long> ids){
        return repo.klausurenMitReferenzen(ids);
    }

}
