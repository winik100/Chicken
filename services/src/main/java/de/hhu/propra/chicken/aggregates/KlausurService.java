package de.hhu.propra.chicken.aggregates;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


public class KlausurService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final KlausurRepository repo;
    private final LsfValidierung lsfValidierung;

    public KlausurService(KlausurRepository repo, LsfValidierung lsfValidierung){
        this.repo = repo;
        this.lsfValidierung = lsfValidierung;
    }

    public void klausurHinzufuegen(Klausur klausur) throws IOException {
        if (!lsfValidierung.gueltigeLsfId(klausur.getLsfId())){
            log.eintragen("Klausurregistrierung", "Registrierungsversuch einer Klausur mit ungültiger LSF-ID.", "ERROR", LocalDateTime.now());
            return;
        }
        Klausur klausurAusDB = repo.klausurMitLsfId(klausur.getLsfId());
        if (klausurAusDB == null) {
            log.eintragen("Klausurregistrierung", "Klausur mit LsfId " + klausur.getLsfId() + " registriert.", "INFO", LocalDateTime.now());
            repo.save(klausur);
        }
        else {
            log.eintragen("Klausurregistrierung", "Klausur mit LsfId " + klausur.getLsfId() + " existiert bereits.", "ERROR", LocalDateTime.now());
        }
    }

    public Klausur findeKlausur(Long lsfId) {
        Klausur klausur = repo.klausurMitLsfId(lsfId);
        return klausur;
    }

    public Set<Klausur> findeKlausurenMitIds(Set<Long> ids){
        return repo.klausurenMitReferenzen(ids);
    }

    public Set<Klausur> alleKlausuren() {
        return repo.alle();
    }
}
