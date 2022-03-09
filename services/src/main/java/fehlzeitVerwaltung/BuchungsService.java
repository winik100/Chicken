package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
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

}
