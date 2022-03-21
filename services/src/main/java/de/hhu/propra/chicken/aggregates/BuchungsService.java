package de.hhu.propra.chicken.aggregates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class BuchungsService {

    private final Logger log = LoggerFactory.getLogger(BuchungsService.class);

    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private final BuchungsValidierung validierung;

    public BuchungsValidierung getValidierung() {
        return validierung;
    }

    public BuchungsService(StudentRepository studentRepository, KlausurRepository klausurRepository, BuchungsValidierung buchungsValidierung) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
        this.validierung = buchungsValidierung;
    }

    static LocalDateTime neuesUrlaubsEndeBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isBefore(freistellungsStart) && (ende.isAfter(freistellungsStart) && ende.isBefore(freistellungsEnde))) {
            return freistellungsStart;
        }
        return ende;
    }

    static LocalDateTime neuenUrlaubsStartBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isAfter(freistellungsStart) && ende.isAfter(freistellungsEnde)) {
            return freistellungsEnde;
        }
        return start;
    }

    public String klausurBuchen(Long lsfId, Long studentID) throws IOException {
        if (!validierung.gueltigeLsfId(lsfId)){
            log.error("Student mit ID " + studentID.toString() + " hat ungültige LSF-ID angegeben.");
            return "Die Veranstaltung mit der angegebenen Veranstaltungs-ID existiert nicht.";
        }
        //KlausurReferenz klausur = new KlausurReferenz(lsfId.getId());
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAnmelden(klausur);
        log.info("Student mit ID " + studentID.toString() + " erfolgreich für Klausur mit ID " + lsfId.toString() + " angemeldet.");
        return "Die Eingabe ist ok.";
    }

    public void klausurStornieren(Long lsfId, Long studentID) {
        //KlausurReferenz klausur = new KlausurReferenz(lsfId.getId());
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
        log.info("Student mit ID " + studentID.toString() + " hat Klausur mit Veranstaltungs-ID " +
                lsfId.toString() + " storniert.");
    }

    public String urlaubBuchen(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        Set<Long> ids = student.getKlausurAnmeldungen();
        Set<Klausur> klausuren = klausurRepository.klausurenMitReferenzen(ids);
        if (!validierung.dauerIstVielfachesVon15(start, ende)) {
            log.error("Student mit ID " + studentID.toString() + " hat ungültige Urlaubsdauer angegeben.");
            return "Die Urlaubsdauer muss ein Vielfaches von 15 sein.";
        }
        if (!validierung.startZeitIstVielfachesVon15(start)) {
            log.error("Student mit ID " + studentID.toString() + " hat ungültige Startzeit angegeben.");
            return "Die Startzeit muss ein Vielfaches von 15 sein.";
        }

        // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
        if (validierung.klausurAmGleichenTag(klausuren, start)) {
            Set<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(klausuren, start, ende);
            if (!ueberschneidendeKlausuren.isEmpty()) {
                // Urlaubszeit an Klausuren anpassen
//                for (Klausur k : ueberschneidendeKlausuren){
//                    student.urlaubAnKlausurAnpassenUndNehmen(k.startFreistellungBerechnen(), k.endeFreistellungBerechnen(),
//                                                                start, ende);
//                }
            }
        }

        if (student.hatUrlaubAm(start.toLocalDate())) {
            if (student.ueberschneidungMitBestehendemUrlaub(start, ende)) {
                log.error("Student mit ID " + studentID.toString() + " hat Urlaub mit Überschneidung angegeben.");
                return "Bestehender Urlaub muss erst storniert werden.";
            }
            if (!validierung.klausurAmGleichenTag(klausuren, start)) {
                if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                    log.error("Student mit ID " + studentID.toString() + " hat Urlaub zu nicht erlaubter Zeit angegeben.");
                    return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen" +
                            "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                }
            }
        }

        if (!validierung.klausurAmGleichenTag(klausuren, start)) {
            if (!validierung.blockEntwederGanzerTagOderMax150Min(start, ende)) {
                log.error("Student mit ID " + studentID.toString() + " hat Urlaub mit nicht erlaubter Dauer angegeben.");
                return "Der Urlaub muss entweder den ganzen Tag oder maximal 150 Minuten dauern.";
            }
        }
        student.urlaubNehmen(start, ende);
        log.info("Student mit ID " + studentID.toString() + " hat Urlaub genommen von " + start + " bis " + ende + ".");
        return "Die Eingabe ist ok.";
    }

    public void urlaubStornieren(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        student.urlaubEntfernen(start, ende);
        log.info("Student mit ID " + studentID.toString() + " hat Urlaub storniert von " + start + " bis " + ende + ".");
    }


}
