package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.StudentRepository;
import de.hhu.propra.chicken.aggregates.StudentenService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class StudentenServiceTest {

    @AfterAll
    static void logLoeschen(){
        File file = new File("auditlog.txt");
        file.delete();
    }

    private final static Student STUDENT_1 = new Student(1L, "student1");

    @Test
    @DisplayName("Wenn Student schon eingetragen ist, wird er nicht gespeichert.")
    void test1() throws IOException {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitGitHubHandle(any())).thenReturn(STUDENT_1);

        service.studentHinzufuegen(STUDENT_1);

        verify(repo, never()).save(STUDENT_1);
    }

    @Test
    @DisplayName("Wenn Student noch nicht eingetragen ist, wird er gespeichert.")
    void test2() throws IOException {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitGitHubHandle(any())).thenReturn(null);

        service.studentHinzufuegen(STUDENT_1);

        verify(repo, times(1)).save(STUDENT_1);
    }

    @Test
    @DisplayName("findeStudent ruft studentMitId aus Repo auf")
    void test3() {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);

        service.findeStudent(145654L);

        verify(repo, times(1)).studentMitId(145654L);
    }

    @Test
    @DisplayName("findeStudentMitHandle ruft studentMitGitHubHandle aus Repo auf")
    void test4() {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);

        service.findeStudentMitHandle("ibimsgithub");

        verify(repo, times(1)).studentMitGitHubHandle("ibimsgithub");
    }

}
