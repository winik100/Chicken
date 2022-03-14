package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.student.Student;
import de.hhu.propra.chicken.repositories.KlausurRepository;
import de.hhu.propra.chicken.repositories.StudentRepository;
import de.hhu.propra.chicken.stereotypes.ApplicationService;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationService
public class BuchungsService {

    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private final BuchungsValidierung validierung = new BuchungsValidierung();


    public BuchungsService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }

    public void klausurBuchen(int lsfId, Long studentID) {
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAnmelden(klausur);
    }

    public void klausurStornieren(int lsfId, Long studentID) {
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
    }

    public void urlaubBuchen1(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        if(validierung.dauerIstVielfachesVon15(start, ende) && validierung.startZeitIstVielfachesVon15(start)){
            if(validierung.klausurAmGleichenTag(student, start)){
                List<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(student, start, ende);
                if(ueberschneidendeKlausuren.isEmpty()) {
                    // überschneidende Urlaube prüfen
                    student.urlaubNehmen(start, ende);
                }
                else {
                    // Urlaubszeit an Klausur(en) anpassen
                    LocalDateTime neuerUrlaubsStart = start;
                    LocalDateTime neuesUrlaubsEnde = ende;
                    for(Klausur k : ueberschneidendeKlausuren) {
                        LocalDateTime freistellungsStart = k.startFreistellungBerechnen();
                        LocalDateTime freistellungsEnde = k.endeFreistellungBerechnen();
                        neuerUrlaubsStart = neuenUrlaubsStartBerechnen(start, ende, freistellungsStart, freistellungsEnde);
                        neuesUrlaubsEnde = neuesUrlaubsEndeBerechnen(start, ende, freistellungsStart, freistellungsEnde);
                    }
                    student.urlaubNehmen(neuerUrlaubsStart, neuesUrlaubsEnde);
                }
            }
            else{
                if(student.hatUrlaubAm(start.toLocalDate())){
                    if(validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                        student.urlaubNehmen(start, ende);
                    }
                }
                else {
                    if(validierung.blockEntwederGanzerTagOderMax150Min(start, ende)){
                        student.urlaubNehmen(start, ende);
                    }
                }
            }
        }
    }

    public String urlaubBuchen (Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        if (validierung.dauerIstVielfachesVon15(start, ende)) {
            if (validierung.startZeitIstVielfachesVon15(start)) {
                // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
                if(validierung.klausurAmGleichenTag(student, start)) {
                    List<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(student, start, ende);
                    if(!ueberschneidendeKlausuren.isEmpty()) {
                        // Urlaubszeit an Klausuren anpassen
                    }
                }

                if (student.hatUrlaubAm(start.toLocalDate())) {
                    if (validierung.ueberschneidungMitBestehendemUrlaub(student, start, ende)) {
                        return "Bestehender Urlaub muss erst storniert werden.";
                    }
                    if (!validierung.klausurAmGleichenTag(student, start)) {
                        if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                            return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen" +
                                    "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                        }
                    }
                }

                if (!validierung.klausurAmGleichenTag(student, start)) {
                    if (!validierung.blockEntwederGanzerTagOderMax150Min(start, ende)) {
                        return "Der Urlaub muss entweder den ganzen Tag oder maximal 150 Minuten dauern.";
                    }
                }
                student.urlaubNehmen(start, ende);
            }
            return "Die Startzeit muss ein Vielfaches von 15 sein.";
        }
        return "Die Urlaubsdauer muss ein Vielfaches von 15 sein.";
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

    public void urlaubStornieren(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        student.urlaubEntfernen(start, ende);
    }

}
