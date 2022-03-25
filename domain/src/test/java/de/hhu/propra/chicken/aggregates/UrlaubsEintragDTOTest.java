package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.UrlaubsEintragDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UrlaubsEintragDTOTest {

    @Test
    @DisplayName("Die Dauer eines Urlaubs von 12 bis 13 beträgt 60 Minuten.")
    void test_1() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 12, 12, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 12, 13, 0);
        UrlaubsEintragDTO urlaubsEintrag = new UrlaubsEintragDTO(urlaubsStart, urlaubsEnde);

        Long dauer = urlaubsEintrag.dauerInMin();

        assertThat(dauer).isEqualTo(60L);
    }
}
