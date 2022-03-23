package de.hhu.propra.chicken.aggregates;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


public class KlausurService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final KlausurRepository repo;

    public KlausurService(KlausurRepository repo){
        this.repo = repo;
    }

    boolean gueltigeLsfId(Long lsfId, Document... document) throws IOException {
        String lsfIdString = lsfId.toString();
        Document doc;
        if (document.length != 0){
            doc = document[0];
        } else {
            doc = Jsoup.connect("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                    + lsfIdString
                    + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung").get();
        }
        String htmlDoc = doc.wholeText();
        return htmlDoc.contains(lsfIdString);
    }

    public void klausurHinzufuegen(Klausur klausur) throws IOException {
        if (gueltigeLsfId(klausur.getLsfId())){
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
