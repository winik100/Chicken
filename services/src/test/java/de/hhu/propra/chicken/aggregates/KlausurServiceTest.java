package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.mockito.Mockito.*;

public class KlausurServiceTest {
    @Test
    @DisplayName("Wenn die Klausur schon eingetragen ist, wird sie nicht gespeichert.")
    void test1() {
        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(any())).thenReturn(PK_12_13);

        service.klausurHinzufuegen(PK_12_13.getId(), PK_12_13.getLsfId(), "Mathe", PK_12_13.getStart(), PK_12_13.getEnde(),"praesenz");

        verify(repo, never()).save(PK_12_13);
    }

    @Test
    @DisplayName("Wenn die Klausur noch nicht eingetragen ist, wird sie gespeichert.")
    void test2() {

        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(any())).thenReturn(null);

        service.klausurHinzufuegen(PK_12_13.getId(), PK_12_13.getLsfId(), "Mathe", PK_12_13.getStart(), PK_12_13.getEnde(), "praesenz");

        verify(repo, times(1)).save(PK_12_13);
    }

    @Test
    @DisplayName("findeKlausur ruft klausurMitId aus Repo auf")
    void test3() {

        KlausurRepository repo = mock(KlausurRepository.class);
        KlausurService service = new KlausurService(repo);

        service.findeKlausur(234567L);

        verify(repo, times(1)).klausurMitLsfId(234567L);
    }

}
