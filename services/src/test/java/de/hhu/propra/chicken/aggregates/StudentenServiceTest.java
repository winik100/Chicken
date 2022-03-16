package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.StudentRepository;
import de.hhu.propra.chicken.aggregates.StudentenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class StudentenServiceTest {

    private final static Student STUDENT_1 = new Student(1L, "student1");

    @Test
    @DisplayName("Wenn Student schon eingetragen ist, wird er nicht gespeichert.")
    void test1() {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitId(any())).thenReturn(STUDENT_1);

        service.studentHinzufuegen(STUDENT_1.getId(), STUDENT_1.getGithubHandle());

        verify(repo, never()).save(STUDENT_1);
    }

    @Test
    @DisplayName("Wenn Student noch nicht eingetragen ist, wird er gespeichert.")
    void test2() {

        StudentRepository repo = mock(StudentRepository.class);
        StudentenService service = new StudentenService(repo);
        when(repo.studentMitId(any())).thenReturn(null);

        service.studentHinzufuegen(STUDENT_1.getId(), STUDENT_1.getGithubHandle());

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


}
