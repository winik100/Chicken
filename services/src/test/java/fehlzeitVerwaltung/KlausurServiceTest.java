package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repositories.KlausurRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class KlausurServiceTest {
    @Test
    @DisplayName("Wenn die Klausur schon eingetragen ist, wird sie nicht gespeichert.")
    void test1() {
        KlausurRepository repo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(234567)).thenReturn(klausur);

        service.klausurHinzufuegen(234567, "Mathe", start, ende, "praesenz");

        verify(repo, never()).save(klausur);
    }

    @Test
    @DisplayName("Wenn die Klausur noch nicht eingetragen ist, wird sie gespeichert.")
    void test2() {

        KlausurRepository repo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        KlausurService service = new KlausurService(repo);
        when(repo.klausurMitLsfId(234567)).thenReturn(null);

        service.klausurHinzufuegen(234567, "Mathe", start, ende, "praesenz");

        verify(repo, times(1)).save(klausur);

    }

    @Test
    @DisplayName("findeKlausur ruft klausurMitId aus Repo auf")
    void test3() {

        KlausurRepository repo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        KlausurService service = new KlausurService(repo);

        service.findeKlausur(234567);

        verify(repo, times(1)).klausurMitLsfId(234567);
    }

}
