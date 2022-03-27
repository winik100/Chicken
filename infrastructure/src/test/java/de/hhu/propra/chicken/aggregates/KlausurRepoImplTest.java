package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql("classpath:init_test_database.sql")
public class KlausurRepoImplTest {

    @Autowired
    DBKlausurRepo repo;

    @Test
    @DisplayName("klausurRepo kann Klausuren aus der Datenbank lesen.")
    @Transactional
    void test_1() {
        KlausurRepoImpl klausurRepo = new KlausurRepoImpl(repo);

        Klausur result = klausurRepo.klausurMitLsfId(999999L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("iwas fuer info");
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2022,3,8,10,0));
        assertThat(result.getEnde()).isEqualTo(LocalDateTime.of(2022,3,8,11,0));
        assertThat(result.getTyp()).isEqualTo("praesenz");
    }

    @Test
    @DisplayName("klausurRepo kann Klausuren aus der Datenbank lesen.")
    @Transactional
    void test_2() {
        KlausurRepoImpl klausurRepo = new KlausurRepoImpl(repo);
        klausurRepo.save(new Klausur(null, 111111L, "test",
                LocalDateTime.of(2022,3,8,11,0),
                LocalDateTime.of(2022,3,8,12,0),
                "online"));

        Klausur result = klausurRepo.klausurMitLsfId(111111L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2022,3,8,11,0));
        assertThat(result.getEnde()).isEqualTo(LocalDateTime.of(2022,3,8,12,0));
        assertThat(result.getTyp()).isEqualTo("online");
    }

    @Test
    @DisplayName("klausurRepoImpl.alle() holt alle Klausuren aus der Datenbank.")
    @Transactional
    void test_3() {
        KlausurRepoImpl klausurRepo = new KlausurRepoImpl(repo);
        Klausur klausur1 = new Klausur(1L, 999999L,"iwas fuer info",
                LocalDateTime.of(2022, 3, 8, 10, 0),
                LocalDateTime.of(2022, 3, 8, 11, 0), "praesenz");
        Klausur klausur2 = new Klausur(2L, 888888L,"iwas anderes fuer info",
                LocalDateTime.of(2022, 3, 8, 11, 0),
                LocalDateTime.of(2022, 3, 8, 12, 0), "praesenz");
        Set<Klausur> klausuren = Set.of(klausur1, klausur2);

        Set<Klausur> klausurenAusDB = klausurRepo.alle();

        assertThat(klausurenAusDB).isEqualTo(klausuren);
    }

    @Test
    @DisplayName("klausurRepoImpl.klausurenMitReferenzen() holt Klausuren mit gegebenen Referenzen aus der Datenbank.")
    @Transactional
    void test_4() {
        KlausurRepoImpl klausurRepo = new KlausurRepoImpl(repo);
        Set<Klausur> klausuren = Set.of(new Klausur(2L, 888888L,"iwas anderes fuer info",
                LocalDateTime.of(2022, 3, 8, 11, 0),
                LocalDateTime.of(2022, 3, 8, 12, 0), "praesenz"));
        Set<Long> referenzen = Set.of(2L);

        Set<Klausur> klausurenAusDB = klausurRepo.klausurenMitReferenzen(referenzen);

        assertThat(klausurenAusDB).isEqualTo(klausuren);
    }
}