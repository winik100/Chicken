package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class StudentServiceTest {

    private final static Student STUDENT_1 = new Student(1L, "student1");
    private final StudentRepository repo = mock(StudentRepository.class);
    private final StudentService service = new StudentService(repo);

    @AfterAll
    static void logLoeschen(){
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Wenn der Student schon eingetragen ist, wird er nicht gespeichert.")
    void test_1() throws IOException {
        when(repo.studentMitGitHubHandle(any())).thenReturn(STUDENT_1);

        service.studentHinzufuegen(STUDENT_1);

        verify(repo, never()).save(STUDENT_1);
    }

    @Test
    @DisplayName("Wenn der Student noch nicht eingetragen ist, wird er gespeichert.")
    void test_2() throws IOException {
        when(repo.studentMitGitHubHandle(any())).thenReturn(null);

        service.studentHinzufuegen(STUDENT_1);

        verify(repo, times(1)).save(STUDENT_1);
    }

    @Test
    @DisplayName("findeStudent ruft studentMitId aus Repo auf.")
    void test_3() {
        service.findeStudent(145654L);

        verify(repo, times(1)).studentMitId(145654L);
    }

    @Test
    @DisplayName("findeStudentMitHandle ruft studentMitGitHubHandle aus Repo auf.")
    void test_4() {
        service.findeStudentMitHandle("ibimsgithub");

        verify(repo, times(1)).studentMitGitHubHandle("ibimsgithub");
    }
}
