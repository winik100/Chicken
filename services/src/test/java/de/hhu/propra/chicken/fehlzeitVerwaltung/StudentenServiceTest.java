package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.student.Student;
import de.hhu.propra.chicken.repositories.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class StudentenServiceTest {


    @Test
    @DisplayName("Wenn Student schon eingetragen ist, wird er nicht gespeichert.")
    void test1() {

        StudentRepository repo = mock(StudentRepository.class);
        Student student = new Student(145654L, "ibimsgithub");
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitId(145654L)).thenReturn(student);

        service.studentHinzufuegen(145654L, "ibimsgithub");

        verify(repo, never()).save(student);
    }

    @Test
    @DisplayName("Wenn Student noch nicht eingetragen ist, wird er gespeichert.")
    void test2() {

        StudentRepository repo = mock(StudentRepository.class);
        Student student = new Student(145654L, "ibimsgithub");
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitId(145654L)).thenReturn(null);

        service.studentHinzufuegen(145654L, "ibimsgithub");

        verify(repo, times(1)).save(student);
    }

    @Test
    @DisplayName("findeStudent ruft studentMitId aus Repo auf")
    void test3() {

        StudentRepository repo = mock(StudentRepository.class);
        Student student = new Student(145654L, "ibimsgithub");
        StudentenService service = new StudentenService(repo);

        service.findeStudent(145654L);

        verify(repo, times(1)).studentMitId(145654L);
    }


}
