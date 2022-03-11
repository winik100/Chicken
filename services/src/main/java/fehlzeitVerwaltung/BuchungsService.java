package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import repositories.KlausurRepository;
import repositories.StudentRepository;
import stereotypes.ApplicationService;

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

    public void urlaubBuchen(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        if(validierung.dauerIstVielfachesVon15(start, ende) && validierung.startZeitIstVielfachesVon15(start)){
            if(validierung.klausurAmGleichenTag(student, start)){
                List<Klausur> klausuren = validierung.ueberschneidungMitKlausur(student, start, ende);
                if(klausuren.isEmpty()) {
                    student.urlaubNehmen(start, ende);
                }
                else {
                    for(Klausur k : klausuren) {
                        LocalDateTime freistellungsStart = k.startFreistellungBerechnen();
                        LocalDateTime freistellungsEnde = k.endeFreistellungBerechnen();
                        LocalDateTime neuerUrlaubsStart = neuenUrlaubsStartBerechnen(start, ende, freistellungsStart, freistellungsEnde);
                        LocalDateTime neuesUrlaubsEnde = neuesUrlaubsEndeBerechnen(start, ende, freistellungsStart, freistellungsEnde);
                    }
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

    LocalDateTime neuesUrlaubsEndeBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isBefore(freistellungsStart) && (ende.isAfter(freistellungsStart) && ende.isBefore(freistellungsEnde))) {
            return freistellungsEnde;
        }
        return ende;
    }

    LocalDateTime neuenUrlaubsStartBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
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
