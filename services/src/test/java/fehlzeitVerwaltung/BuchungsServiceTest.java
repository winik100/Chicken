package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.KlausurRepository;
import repositories.StudentRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BuchungsServiceTest {

    @Test
    @DisplayName("Buchungsservice.klausurbuchen fügt Klausuranmeldung zu Student hinzu")
    void test_1() {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        StudentRepository studentRepo = mock(StudentRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(anyInt())).thenReturn(klausur);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.klausurBuchen(234567, 10L);

        assertThat(student.getKlausurAnmeldungen()).contains(klausur);
    }

    @Test
    @DisplayName("Buchungsservice.klausurbuchen entfernt Klausuranmeldung von Student")
    void test_2() {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        StudentRepository studentRepo = mock(StudentRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(anyInt())).thenReturn(klausur);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);
        buchungsService.klausurBuchen(234567, 10L);

        buchungsService.klausurStornieren(234567, 10L);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(klausur);
    }

    @Test
    @DisplayName("BuchungsService.urlaubBuchen() ruft Student.urlaubNehmen() korrekt auf")
    void test_3() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.urlaubBuchen(student.getId(), start, ende);

        verify(student, times(1)).urlaubNehmen(start, ende);
    }

    @Test
    @DisplayName("BuchungsService.urlaubStornieren() ruft Student.urlaubEntfernen() korrekt auf")
    void test_4() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);
        buchungsService.urlaubBuchen(student.getId(), start, ende);

        buchungsService.urlaubStornieren(student.getId(), start, ende);

        verify(student, times(1)).urlaubEntfernen(start, ende);
    }
}
