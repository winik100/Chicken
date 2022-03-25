package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static de.hhu.propra.chicken.util.KlausurTemplates.PK_12_13;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {

    KlausurRepository repo = mock(KlausurRepository.class);
    LsfValidierung lsfValidierung = mock(LsfValidierung.class);
    BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
    KlausurService service = new KlausurService(repo, lsfValidierung, buchungsValidierung);

    @AfterAll
    static void logLoeschen() {
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Wenn die Klausur schon eingetragen ist, wird sie nicht gespeichert.")
    void test_1() throws IOException {
        when(repo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(lsfValidierung.gueltigeLsfId(any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);

        service.klausurHinzufuegen(PK_12_13);

        verify(repo, never()).save(PK_12_13);
    }

    @Test
    @DisplayName("Wenn die Klausur noch nicht eingetragen ist, wird sie gespeichert.")
    void test_2() throws IOException {
        when(repo.klausurMitLsfId(any())).thenReturn(null);
        when(lsfValidierung.gueltigeLsfId(any())).thenReturn(true);
        when(buchungsValidierung.klausurLiegtImPraktikumsZeitraum(any())).thenReturn(true);

        service.klausurHinzufuegen(PK_12_13);

        verify(repo, times(1)).save(PK_12_13);
    }

    @Test
    @DisplayName("findeKlausur ruft klausurMitLsfId aus Repo auf.")
    void test_3() {
        service.findeKlausurMitLsfId(234567L);

        verify(repo, times(1)).klausurMitLsfId(234567L);
    }
}
