package klausur;

import klausur.Klausur;
import klausur.KlausurTyp;
import klausur.LsfId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class KlausurTest {

    @Test
    @DisplayName("Berechnung der Dauer korrekt")
    void test1() {

        LsfId lsfId = new LsfId(234567);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(111, lsfId, "Mathe", start, ende, KlausurTyp.PRAESENZ);

        Long dauer = klausur.dauer();

        assertThat(dauer).isEqualTo(60L);



    }

}
