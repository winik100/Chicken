package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {

    @AfterAll
    static void logLoeschen(){
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Wenn die Klausur schon eingetragen ist, wird sie nicht gespeichert.")
    void test1() throws IOException {
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(any())).thenReturn(PK_12_13);

        service.klausurHinzufuegen(PK_12_13);

        verify(repo, never()).save(PK_12_13);
    }

    @Test
    @DisplayName("Wenn die Klausur noch nicht eingetragen ist, wird sie gespeichert.")
    void test2() throws IOException {
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(any())).thenReturn(null);

        service.klausurHinzufuegen(PK_12_13);

        verify(repo, times(1)).save(PK_12_13);
    }

    @Test
    @DisplayName("findeKlausur ruft klausurMitId aus Repo auf")
    void test3() throws IOException {

        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);

        service.findeKlausur(234567L);

        verify(repo, times(1)).klausurMitLsfId(234567L);
    }

}
