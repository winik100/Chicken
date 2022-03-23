package de.hhu.propra.chicken;

import de.hhu.propra.chicken.aggregates.DBKlausurRepo;
import de.hhu.propra.chicken.aggregates.DBStudentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableAutoConfiguration(exclude = JdbcRepositoriesAutoConfiguration.class)
@ActiveProfiles("test")
class ChickenApplicationTests {

    @MockBean
    DBKlausurRepo klausurRepo;

    @MockBean
    DBStudentRepo studentRepo;

    @Test
    void contextLoads() {
    }

}
