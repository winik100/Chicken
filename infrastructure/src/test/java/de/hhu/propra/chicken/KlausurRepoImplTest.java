package de.hhu.propra.chicken;

import de.hhu.propra.chicken.aggregates.DBKlausurRepo;
import de.hhu.propra.chicken.aggregates.Klausur;
import de.hhu.propra.chicken.aggregates.KlausurRepoImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ActiveProfiles("test")
@Sql("classpath:init_test.sql")
public class KlausurRepoImplTest {

    @Autowired
    DBKlausurRepo repo;


    @Test
    @DisplayName("klausurRepo kann in Datenbank einfügen und aus ihr lesen")
    void test_1(){
        KlausurRepoImpl klausurRepo = new KlausurRepoImpl(repo);
        //id ist null, da autogeneriert
        klausurRepo.save(new Klausur(null, 111111L, "test",
                LocalDateTime.of(2022,3,8,11,0),
                LocalDateTime.of(2022,3,8,12,0),
                "praesenz"));
        Klausur result = klausurRepo.klausurMitLsfId(111111L);
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2022,3,8,11,0));
        assertThat(result.getEnde()).isEqualTo(LocalDateTime.of(2022,3,8,12,0));
        assertThat(result.getTyp()).isEqualTo("praesenz");
    }
}