package aggregates.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlaubszeitTest {

    @Test
    @DisplayName("240 Minuten Gesamturlaub - 15 Minuten = 225 Resturlaub")
    void test1() {
        Urlaubszeit urlaubszeit = new Urlaubszeit();
        urlaubszeit.zeitEntfernen(15L);
        assertThat(urlaubszeit.getMinuten()).isEqualTo(225L);
    }

    @Test
    @DisplayName("240 Minuten Gesamturlaub - 30 Minuten + 15 Minuten = 225 Resturlaub")
    void test2() {
        Urlaubszeit urlaubszeit = new Urlaubszeit();
        urlaubszeit.zeitEntfernen(30L);
        urlaubszeit.zeitHinzufuegen(15L);
        assertThat(urlaubszeit.getMinuten()).isEqualTo(225L);
    }

    @Test
    @DisplayName("Urlaubszeit kann nicht negativ werden")
    void test3() {
        Urlaubszeit urlaubszeit = new Urlaubszeit();
        urlaubszeit.zeitEntfernen(240L);
        urlaubszeit.zeitEntfernen(15L);
        assertThat(urlaubszeit.getMinuten()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Urlaubszeit kann nicht größer als 240 Minuten sein")
    void test4() {
        Urlaubszeit urlaubszeit = new Urlaubszeit();
        urlaubszeit.zeitHinzufuegen(60L);
        assertThat(urlaubszeit.getMinuten()).isEqualTo(240L);
    }

}
