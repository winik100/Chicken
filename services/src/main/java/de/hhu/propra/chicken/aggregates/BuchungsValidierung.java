package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Klausur;
import de.hhu.propra.chicken.aggregates.Student;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BuchungsValidierung {

    private final static LocalTime START = LocalTime.of(9, 30);
    private final static LocalTime ENDE = LocalTime.of(13, 30);

    boolean dauerIstVielfachesVon15(LocalDateTime start, LocalDateTime ende) {
        long dauer = Duration.between(start, ende).toMinutes();
        return (dauer % 15) == 0;
    }

    boolean startZeitIstVielfachesVon15(LocalDateTime start) {
        return (start.getMinute() % 15) == 0;
    }

    boolean klausurAmGleichenTag(Set<Klausur> klausurAnmeldungen, LocalDateTime start) {
        LocalDate datum = start.toLocalDate();
        Set<LocalDate> klausurDaten =
                klausurAnmeldungen.stream().map(Klausur::getStart).map(LocalDateTime::toLocalDate).collect(Collectors.toSet());
        return klausurDaten.contains(datum);
    }

    boolean blockEntwederGanzerTagOderMax150Min(LocalDateTime start, LocalDateTime ende) {
        long dauer = Duration.between(start, ende).toMinutes();
        return (dauer == 240 || dauer <= 150);
    }

    boolean mind90MinZwischenUrlauben(Student student, LocalDateTime startZweiterUrlaub, LocalDateTime endeZweiterUrlaub) {
        LocalDate tag = startZweiterUrlaub.toLocalDate();
        LocalDateTime startErsterUrlaub = student.startDesUrlaubsAm(tag);
        LocalDateTime endeErsterUrlaub = student.endeDesUrlaubsAm(tag);
        if (istAmAnfangDesTages(startErsterUrlaub)) {
            Long zeitZwischenUrlauben = Duration.between(endeErsterUrlaub, startZweiterUrlaub).toMinutes();
            if (istAmEndeDesTages(endeZweiterUrlaub) && zeitZwischenUrlauben >= 90) {
                return true;
            }
        } else if (istAmEndeDesTages(endeErsterUrlaub)) {
            Long zeitZwischenUrlauben = Duration.between(endeZweiterUrlaub, startErsterUrlaub).toMinutes();
            if (istAmAnfangDesTages(startZweiterUrlaub) && zeitZwischenUrlauben >= 90) {
                return true;
            }
        }
        return false;
    }

    boolean istAmEndeDesTages(LocalDateTime zeit) {
        LocalDate tag = zeit.toLocalDate();
        return zeit.equals(LocalDateTime.of(tag, ENDE));
    }

    boolean istAmAnfangDesTages(LocalDateTime zeit) {
        LocalDate tag = zeit.toLocalDate();
        return zeit.equals(LocalDateTime.of(tag, START));
    }

    Set<Klausur> ueberschneidungMitKlausur(Set<Klausur> klausuren, LocalDateTime urlaubsStart, LocalDateTime urlaubsEnde) {
        Set<Klausur> klausurenMitUeberschneidung = new HashSet<>();
        Set<Klausur> klausurenAnDemTag = klausuren.stream().filter(x -> x.getStart().toLocalDate().equals(urlaubsStart.toLocalDate())).collect(Collectors.toSet());
        for(Klausur k : klausurenAnDemTag) {
            LocalDateTime startFreistellung = k.startFreistellungBerechnen();
            LocalDateTime endeFreistellung = k.endeFreistellungBerechnen();
            if (startFreistellung.isAfter(urlaubsStart.minusMinutes(1)) && startFreistellung.isBefore((urlaubsEnde))) {
                klausurenMitUeberschneidung.add(k);
            }
            if (endeFreistellung.isAfter(urlaubsStart) && endeFreistellung.minusMinutes(1).isBefore(urlaubsEnde)){
                klausurenMitUeberschneidung.add(k);
            }
            if (startFreistellung.isBefore(urlaubsStart) && endeFreistellung.isAfter(urlaubsEnde)){
                klausurenMitUeberschneidung.add(k);
            }
        }
        return klausurenMitUeberschneidung;
    }

}
