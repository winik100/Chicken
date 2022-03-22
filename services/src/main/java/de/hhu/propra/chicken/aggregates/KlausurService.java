package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.DomainService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class KlausurService {
    private final KlausurRepository repo;

    public KlausurService(KlausurRepository repo) {
        this.repo = repo;
    }

    public void klausurHinzufuegen(Long lsfId, String name, LocalDateTime start, LocalDateTime ende, String typ) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        if (klausur == null) {
            klausur = new Klausur(null, lsfId, name, start, ende, typ);
            repo.save(klausur);
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
