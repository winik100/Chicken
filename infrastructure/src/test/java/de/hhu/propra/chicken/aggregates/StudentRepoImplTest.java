package de.hhu.propra.chicken.aggregates;


import de.hhu.propra.chicken.util.KlausurReferenz;
import de.hhu.propra.chicken.util.UrlaubsEintragDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql("classpath:/init_test_database.sql")
public class StudentRepoImplTest {

    @Autowired
    DBStudentRepo studentRepo;

    @Autowired
    DBKlausurRepo klausurRepo;

    @Test
    @DisplayName("StudentRepo kann Studenten anhand des Github-Handles aus der Datenbank lesen.")
    @Transactional
    void test_1() {
        StudentRepoImpl studentRepoImpl = new StudentRepoImpl(studentRepo, klausurRepo);
        UrlaubsEintragDTO ersterUrlaub = new UrlaubsEintragDTO(LocalDateTime.of(2022, 3, 17, 9, 30),
                LocalDateTime.of(2022, 3, 17, 11, 30));
        UrlaubsEintragDTO zweiterUrlaub = new UrlaubsEintragDTO(LocalDateTime.of(2022, 3, 18, 10, 30),
                LocalDateTime.of(2022, 3, 18, 12, 30));

        Student studentAusDB = studentRepoImpl.studentMitGitHubHandle("testhandle_3");

        assertThat(studentAusDB.getId()).isEqualTo(3L);
        assertThat(studentAusDB.getGithubHandle()).isEqualTo("testhandle_3");
        assertThat(studentAusDB.getKlausurAnmeldungen()).isEqualTo(Set.of(2L));
        assertThat(studentAusDB.getUrlaubeAlsDTOs()).isEqualTo(Set.of(ersterUrlaub, zweiterUrlaub));
        assertThat(studentAusDB.getResturlaubInMin()).isEqualTo(0L);
    }

    @Test
    @DisplayName("StudentRepo kann Studenten anhand des Github-Handles in die Datenbank einfügen")
    @Transactional
    void test_2() {
        StudentRepoImpl studentRepoImpl = new StudentRepoImpl(studentRepo, klausurRepo);
        UrlaubsEintragDTO urlaub = new UrlaubsEintragDTO(LocalDateTime.of(2022, 3, 17, 10, 30),
                LocalDateTime.of(2022, 3, 17, 11, 15));
        Student student = new Student(null, "testhandle_5", 195L,
                Set.of(urlaub), Stream.of(1L, 2L).map(KlausurReferenz::new).collect(Collectors.toSet()));

        studentRepoImpl.save(student);

        Student studentAusDB = studentRepoImpl.studentMitGitHubHandle("testhandle_5");
        assertThat(studentAusDB.getId()).isEqualTo(5L);
        assertThat(studentAusDB.getGithubHandle()).isEqualTo("testhandle_5");
        assertThat(studentAusDB.getKlausurAnmeldungen()).isEqualTo(Set.of(1L, 2L));
        assertThat(studentAusDB.getUrlaubeAlsDTOs()).isEqualTo(Set.of(urlaub));
        assertThat(studentAusDB.getResturlaubInMin()).isEqualTo(195L);
    }
}
