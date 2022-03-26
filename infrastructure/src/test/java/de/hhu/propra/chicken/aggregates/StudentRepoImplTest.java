package de.hhu.propra.chicken.aggregates;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql("classpath:/init_studentrepo_test.sql")
public class StudentRepoImplTest {

    @Autowired
    DBStudentRepo studentRepo;

    @Autowired
    DBKlausurRepo klausurRepo;

    @Test
    @DisplayName("StudentRepo kann Studenten anhand des Github-Handles in die Datenbank einfügen und aus ihr lesen.")
    @Transactional
    void test_1() {
        StudentRepoImpl studentRepoImpl = new StudentRepoImpl(studentRepo, klausurRepo);

        Student studentAusDB = studentRepoImpl.studentMitGitHubHandle("testhandle_1");

        assertThat(studentAusDB.getGithubHandle()).isEqualTo("testhandle_1");
        assertThat(studentAusDB.getKlausurAnmeldungen()).isEqualTo(Collections.emptySet());
        assertThat(studentAusDB.getUrlaubeAlsDTOs()).isEqualTo(Collections.emptySet());
        assertThat(studentAusDB.getResturlaubInMin()).isEqualTo(240L);
    }

    @Test
    @DisplayName("StudentRepo kann Studenten anhand des Github-Handles in die Datenbank einfügen und aus ihr lesen.")
    @Transactional
    void test_2() {
        StudentRepoImpl studentRepoImpl = new StudentRepoImpl(studentRepo, klausurRepo);
        Student student = new Student("testhandle_1200");

        studentRepoImpl.save(student);

        Student studentAusDB = studentRepoImpl.studentMitGitHubHandle("testhandle_1200");
        assertThat(studentAusDB.getGithubHandle()).isEqualTo("testhandle_1200");
        assertThat(studentAusDB.getKlausurAnmeldungen()).isEqualTo(Collections.emptySet());
        assertThat(studentAusDB.getUrlaubeAlsDTOs()).isEqualTo(Collections.emptySet());
        assertThat(studentAusDB.getResturlaubInMin()).isEqualTo(240L);
    }
}
