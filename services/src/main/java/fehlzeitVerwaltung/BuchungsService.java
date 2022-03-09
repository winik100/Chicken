package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import java.time.LocalDateTime;
import repositories.KlausurRepository;
import repositories.StudentRepository;

public class BuchungsService {

   private final StudentRepository studentRepository;
   private final KlausurRepository klausurRepository;


    public BuchungsService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }

    public void klausurBuchen(int lsfId, Long studentID){
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAnmelden(klausur);
    }

    public void klausurStornieren(int lsfId, Long studentID){
        Klausur klausur = klausurRepository.klausurMitLsfId(lsfId);
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
    }

    public void urlaubBuchen(Long studentID, LocalDateTime start, LocalDateTime ende){
        Student student = studentRepository.studentMitId(studentID);
        student.urlaubNehmen(start, ende);
    }

    public void urlaubStornieren(Long studentID, LocalDateTime start, LocalDateTime ende){
      Student student = studentRepository.studentMitId(studentID);
      student.urlaubEntfernen(start, ende);
    }

}
