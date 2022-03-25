package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.AuditLog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

public class BuchungsService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final StudentRepository studentRepo;
    private final KlausurRepository klausurRepo;
    private final BuchungsValidierung validierung;

    public BuchungsService(StudentRepository studentRepo,
                           KlausurRepository klausurRepo,
                           BuchungsValidierung validierung) throws IOException {
        this.studentRepo = studentRepo;
        this.klausurRepo = klausurRepo;
        this.validierung = validierung;
    }

    static LocalDateTime neuesUrlaubsEndeBerechnen(LocalDateTime start, LocalDateTime ende,
                                                   LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isBefore(freistellungsStart)
                && (ende.isAfter(freistellungsStart) && ende.isBefore(freistellungsEnde))) {
            return freistellungsStart;
        }
        return ende;
    }

    static LocalDateTime neuenUrlaubsStartBerechnen(LocalDateTime start, LocalDateTime ende,
                                                    LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isAfter(freistellungsStart) && ende.isAfter(freistellungsEnde)) {
            return freistellungsEnde;
        }
        return start;
    }

    public String klausurBuchen(Klausur klausur, Student student) throws IOException {
        if (student.ueberschneidungKlausurMitBestehendemUrlaub(klausur)) {
            student.bestehendenUrlaubAnKlausurAnpassen(klausur);
        }
        student.klausurAnmelden(klausur);
        studentRepo.save(student);
        log.info(student.getGithubHandle(), "Erfolgreiche Anmeldung der Klausur mit LSF-ID "
                + klausur.getLsfId() + ".", LocalDateTime.now());
        return "";
    }

    public String klausurStornieren(Klausur klausur, Student student) throws IOException {
        if (!validierung.buchungLiegtNachZeitpunkt(klausur.startFreistellungBerechnen(),
                LocalDateTime.now().plusDays(1))) {
            log.error(student.getGithubHandle(), "Verspäteter Stornierungsversuch der Klausur mit LSF-ID "
                    + klausur.getLsfId() + ".", LocalDateTime.now());
            return "Klausur kann nur bis zum Vortag storniert werden.";
        }
        student.klausurAbmelden(klausur);
        studentRepo.save(student);
        log.info(student.getGithubHandle(), "Erfolgreiche Stornierung der Klausur mit LSF-ID "
                + klausur.getLsfId() + ".", LocalDateTime.now());
        return "";
    }

    public String urlaubBuchen(Student student, LocalDateTime start, LocalDateTime ende) throws IOException {
        Set<Long> ids = student.getKlausurAnmeldungen();
        Set<Klausur> klausuren = klausurRepo.klausurenMitReferenzen(ids);

        if (!validierung.buchungLiegtNachZeitpunkt(start, LocalDateTime.now())) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub in der Vergangenheit.",
                    LocalDateTime.now());
            return "Der gewünschte Urlaub darf nicht in der Vergangenheit liegen!";
        }

        if (!validierung.liegtImPraktikumsZeitraum(start, ende)) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub außerhalb der Praktikumszeit.",
                    LocalDateTime.now());
            return "Der gewünschte Urlaub liegt ausserhalb der Praktikumszeit. Gültig sind Mo. - Fr. im Zeitraum vom "
                    + validierung.startTag + " bis " + validierung.endTag + " zwischen " + validierung.startZeit
                    + " und " + validierung.endZeit + ".";
        }

        if (!validierung.dauerMindestens15Min(start, ende)) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub kürzer als 15 Minuten.",
                    LocalDateTime.now());
            return "Die Urlaubsdauer muss mindestens 15 Minuten betragen.";
        }

        if (!validierung.dauerIstVielfachesVon15(start, ende)) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub mit ungültiger Dauer.",
                    LocalDateTime.now());
            return "Die Urlaubsdauer muss ein Vielfaches von 15 sein.";
        }

        if (!validierung.startZeitIstVielfachesVon15(start)) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub mit ungültiger Startzeit.",
                    LocalDateTime.now());
            return "Die Startzeit muss ein Vielfaches von 15 sein.";
        }

        // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
        if (validierung.klausurAmGleichenTag(klausuren, start)) {
            Set<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(klausuren, start, ende);
            if (!ueberschneidendeKlausuren.isEmpty()) {
                student.urlaubAnKlausurAnpassenUndNehmen(ueberschneidendeKlausuren, start, ende);
                studentRepo.save(student);
                return "";
            }
        }

        if (student.hatUrlaubAm(start.toLocalDate())) {
            if (student.ueberschneidungMitBestehendemUrlaub(start, ende)) {
                student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start, ende);
                studentRepo.save(student);
                return "";
            }
            if (!validierung.klausurAmGleichenTag(klausuren, start)) {
                if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                    log.error(student.getGithubHandle(), "Buchungsversuch von zweitem Urlaub am selben Tag, " +
                            "ohne Bedingungen einzuhalten.", LocalDateTime.now());
                    return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen " +
                            "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                }
            }
        }

        if (!validierung.klausurAmGleichenTag(klausuren, start)) {
            if (!validierung.blockEntwederGanzerTagOderMax150Min(start, ende)) {
                log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub mit Dauer länger als " +
                        "150 Minuten, aber nicht den ganzen Tag", LocalDateTime.now());
                return "Der Urlaub muss entweder den ganzen Tag oder maximal 150 Minuten dauern.";
            }
        }

        if (!validierung.hatAusreichendRestUrlaub(student, start, ende)) {
            log.error(student.getGithubHandle(), "Buchungsversuch von Urlaub, dessen Dauer den verbleibenden " +
                    "Resturlaub übersteigt.", LocalDateTime.now());
            return  "Ihr Resturlaub reicht nicht aus.";
        }
        student.urlaubNehmen(start, ende);
        studentRepo.save(student);
        return "";
    }

    public String urlaubStornieren(Student student, LocalDateTime start, LocalDateTime ende) throws IOException {
        if (!validierung.buchungLiegtNachZeitpunkt(start, LocalDateTime.now().plusDays(1))) {
            log.error(student.getGithubHandle(), "Verspäteter Stornierungsversuch des Urlaubs am "
                    + start.toLocalDate() + " von " + start.toLocalTime() + " bis " + ende.toLocalTime() + ".",
                    LocalDateTime.now());
            return "Urlaub kann nur bis zum Vortag storniert werden.";
        }
        student.urlaubEntfernen(start, ende);
        studentRepo.save(student);
        log.info(student.getGithubHandle(), "Erfolgreiche Stornierung von Urlaub am " + start.toLocalDate()
                + " von " + start.toLocalTime() + " bis " + ende.toLocalTime() + ".", LocalDateTime.now());
        return "";
    }
}
