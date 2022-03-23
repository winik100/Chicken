package de.hhu.propra.chicken.aggregates;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


public class BuchungsService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private final BuchungsValidierung validierung;

    public BuchungsService(StudentRepository studentRepository, KlausurRepository klausurRepository, BuchungsValidierung buchungsValidierung) throws IOException {
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
        Student student = studentRepository.studentMitId(studentID);
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        student.klausurAnmelden(klausur);
        log.eintragen(student.getGithubHandle(), "Erfolgreiche Anmeldung der Klausur mit LSF-ID " + lsfId + ".", "INFO", LocalDateTime.now());
        return "";
    }

    public String klausurStornieren(Long lsfId, Long studentID) throws IOException {
        //KlausurReferenz klausur = new KlausurReferenz(lsfId.getId());
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
        log.eintragen(student.getGithubHandle(), "Erfolgreiche Stornierung der Klausur mit LSF-ID " + lsfId + ".", "INFO", LocalDateTime.now());
        return "";
    }

    public String urlaubBuchen(Long studentID, LocalDateTime start, LocalDateTime ende) throws IOException {
        Student student = studentRepository.studentMitId(studentID);
        Set<Long> ids = student.getKlausurAnmeldungen();
        Set<Klausur> klausuren = klausurRepository.klausurenMitReferenzen(ids);

        if (!validierung.dauerIstVielfachesVon15(start, ende)) {
            log.eintragen(student.getGithubHandle(), "Buchungsversuch von Urlaub mit ungültiger Dauer.", "ERROR", LocalDateTime.now());
            return "Die Urlaubsdauer muss ein Vielfaches von 15 sein.";
        }
        if (!validierung.startZeitIstVielfachesVon15(start)) {
            log.eintragen(student.getGithubHandle(), "Buchungsversuch von Urlaub mit ungültiger Startzeit.", "ERROR", LocalDateTime.now());
            return "Die Startzeit muss ein Vielfaches von 15 sein.";
        }

        // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
        if (validierung.klausurAmGleichenTag(klausuren, start)) {
            Set<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(klausuren, start, ende);
            if (!ueberschneidendeKlausuren.isEmpty()) {
                // Urlaubszeit an Klausuren anpassen
                student.urlaubAnKlausurAnpassenUndNehmen(ueberschneidendeKlausuren, start, ende);
            }
        }

        if (student.hatUrlaubAm(start.toLocalDate())) {
            if (student.ueberschneidungMitBestehendemUrlaub(start, ende)) {
                log.eintragen(student.getGithubHandle(), "Buchungsversuch von Urlaub mit Überschneidung.", "ERROR", LocalDateTime.now());
                return "Bestehender Urlaub muss erst storniert werden.";
            }
            if (!validierung.klausurAmGleichenTag(klausuren, start)) {
                if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                    log.eintragen(student.getGithubHandle(), "Buchungsversuch von zweitem Urlaub am selben Tag, ohne Bedingungen einzuhalten.", "ERROR", LocalDateTime.now());
                    return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen" +
                            "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                }
            }
        }

        if (!validierung.klausurAmGleichenTag(klausuren, start)) {
            if (!validierung.blockEntwederGanzerTagOderMax150Min(start, ende)) {
                log.eintragen(student.getGithubHandle(), "Buchungsversuch von Urlaub mit längerer Dauer als 150 Minuten, aber nicht den ganzen Tag", "ERROR", LocalDateTime.now());
                return "Der Urlaub muss entweder den ganzen Tag oder maximal 150 Minuten dauern.";
            }
        }

        if (!validierung.hatAusreichendRestUrlaub(student, start, ende)) {
            log.eintragen(student.getGithubHandle(), "Buchungsversuch von Urlaub, dessen Dauer den verbleibenden Resturlaub übersteigt.", "ERROR", LocalDateTime.now());
            return  "Ihr Resturlaub reicht nicht aus.";
        }
        student.urlaubNehmen(start, ende);
        log.eintragen(student.getGithubHandle(), "Erfolgreiche Buchung von Urlaub am " + start.toLocalDate() + " von " + start.toLocalTime() + " bis " + ende.toLocalTime() + ".", "INFO", LocalDateTime.now());
        return "";
    }

    public String urlaubStornieren(Long studentID, LocalDateTime start, LocalDateTime ende) throws IOException {
        Student student = studentRepository.studentMitId(studentID);
        student.urlaubEntfernen(start, ende);
        log.eintragen(student.getGithubHandle(), "Erfolgreiche Stornierung von Urlaub am " + start.toLocalDate() + " von " + start.toLocalTime() + " bis " + ende.toLocalTime() + ".", "INFO", LocalDateTime.now());
        return "";
    }


}
