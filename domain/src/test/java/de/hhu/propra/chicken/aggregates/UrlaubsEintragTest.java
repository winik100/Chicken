package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UrlaubsEintragTest {

    @Test
    @DisplayName("dauerInMin() bei Urlaub von 12:00 bis 13:00 beträgt 60 Minuten.")
    void test_1(){
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 12, 12, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 12, 13, 0);
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(urlaubsStart, urlaubsEnde);

        Long dauer = urlaubsEintrag.dauerInMin();

        assertThat(dauer).isEqualTo(60L);
    }
}
