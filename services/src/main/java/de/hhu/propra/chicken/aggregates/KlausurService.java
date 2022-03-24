package de.hhu.propra.chicken.aggregates;


import de.hhu.propra.chicken.util.AuditLog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


public class KlausurService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final KlausurRepository repo;
    private final LsfValidierung lsfValidierung;
    private final BuchungsValidierung buchungsValidierung;

    public KlausurService(KlausurRepository repo, LsfValidierung lsfValidierung, BuchungsValidierung buchungsValidierung){
        this.repo = repo;
        this.lsfValidierung = lsfValidierung;
        this.buchungsValidierung = buchungsValidierung;
    }

    public String klausurHinzufuegen(Klausur klausur) throws IOException {
        if (!buchungsValidierung.liegtImPraktikumsZeitraum(klausur.startFreistellungBerechnen(), klausur.endeFreistellungBerechnen())){
            log.error("Klausurregistrierung", "Registrierungsversuch einer Klausur außerhalb des Praktikumszeitraums", LocalDateTime.now());
            return "Die Klausur liegt ausserhalb der Praktikumszeit. Gültig sind Mo. - Fr. im Zeitraum vom " + buchungsValidierung.startTag + " bis "
                    + buchungsValidierung.endTag + " zwischen " + buchungsValidierung.startZeit + " und " + buchungsValidierung.endZeit + ".";
        }
        if (!lsfValidierung.gueltigeLsfId(klausur.getLsfId())){
            log.error("Klausurregistrierung", "Registrierungsversuch einer Klausur mit ungültiger LSF-ID.", LocalDateTime.now());
            return "Es gibt keine Veranstaltung mit der angegebenen LSF-ID.";
        }
        Klausur klausurAusDB = repo.klausurMitLsfId(klausur.getLsfId());
        if (klausurAusDB == null) {
            log.info("Klausurregistrierung", "Klausur mit LsfId " + klausur.getLsfId() + " registriert.", LocalDateTime.now());
            repo.save(klausur);
        }
        else {
            log.error("Klausurregistrierung", "Klausur mit LsfId " + klausur.getLsfId() + " existiert bereits.", LocalDateTime.now());
        }
        return "";
    }

    public Klausur findeKlausurMitLsfId(Long lsfId) {
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
