package de.hhu.propra.chicken.aggregates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LsfValidierungTest {

    @Test
    @DisplayName("Bei existierender Veranstaltung wird die VeranstaltungsID auf der geparsten Seite gefunden.")
    void test_1() throws IOException {
        LsfValidierung lsfValidierung = new LsfValidierung();
        InputStream resourceAsStream = BuchungsValidierungTests.class.getResourceAsStream("/valideVeranstaltung.html");
        Document parsedDoc = Jsoup.parse(resourceAsStream, StandardCharsets.UTF_8.name(), "");

        boolean b = lsfValidierung.gueltigeLsfId(219960L, parsedDoc);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Bei nicht existierender Veranstaltung wird die VeranstaltungsID nicht auf der geparsten Seite gefunden.")
    void test_2() throws IOException {
        LsfValidierung lsfValidierung = new LsfValidierung();
        InputStream resourceAsStream = BuchungsValidierungTests.class.getResourceAsStream("/invalideVeranstaltung.html");
        Document parsedDoc = Jsoup.parse(resourceAsStream, StandardCharsets.UTF_8.name(), "");

        boolean b = lsfValidierung.gueltigeLsfId(519960L, parsedDoc);

        assertThat(b).isFalse();
    }
}
