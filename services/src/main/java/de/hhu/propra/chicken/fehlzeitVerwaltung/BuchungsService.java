package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.student.KlausurReferenz;
import de.hhu.propra.chicken.aggregates.student.Student;
import de.hhu.propra.chicken.aggregates.urlaub.UrlaubsEintrag;
import de.hhu.propra.chicken.repositories.KlausurRepository;
import de.hhu.propra.chicken.repositories.StudentRepository;
import de.hhu.propra.chicken.stereotypes.ApplicationService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        KlausurReferenz klausur = new KlausurReferenz(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAnmelden(klausur);
    }

    public void klausurStornieren(int lsfId, Long studentID) {
        KlausurReferenz klausur = new KlausurReferenz(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
    }

    public String urlaubBuchen (Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        Set<Klausur> klausuren = klausurRepository.klausurenMitReferenzen(student.getKlausurAnmeldungen());
        if (validierung.dauerIstVielfachesVon15(start, ende)) {
            if (validierung.startZeitIstVielfachesVon15(start)) {
                // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
                if(validierung.klausurAmGleichenTag(klausuren, start)) {
                    Set<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(klausuren, start, ende);
                    if(!ueberschneidendeKlausuren.isEmpty()) {
                        // Urlaubszeit an Klausuren anpassen
                    }
                }

                if (student.hatUrlaubAm(start.toLocalDate())) {
                    if (validierung.ueberschneidungMitBestehendemUrlaub(student, start, ende)) {
                        return "Bestehender Urlaub muss erst storniert werden.";
                    }
                    if (!validierung.klausurAmGleichenTag(klausuren, start)) {
                        if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                            return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen" +
                                    "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                        }
                    }
                }

                if (!validierung.klausurAmGleichenTag(klausuren, start)) {
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

//    public Set<UrlaubsEintrag> urlaubAnKlausurenAnpassen(Set<Klausur> klausuren, LocalDateTime geplanterStart, LocalDateTime geplantesEnde) {
//        UrlaubsEintrag urlaub = new UrlaubsEintrag(geplanterStart, geplantesEnde);
//        Set<UrlaubsEintrag> angepassteUrlaubsBloecke = new HashSet<>();
//        angepassteUrlaubsBloecke.add(urlaub);
//        for (Klausur k : klausuren) {
//            for (UrlaubsEintrag u : angepassteUrlaubsBloecke) {
//                if ()
//            }
//        }
//    }

}
