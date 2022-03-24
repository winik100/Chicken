package de.hhu.propra.chicken.aggregates;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class BuchungsValidierung {

    final LocalTime startZeit;
    final LocalTime endZeit;
    final LocalDate startTag;
    final LocalDate endTag;


    public BuchungsValidierung(String startZeit, String endZeit,
                               String startTag, String endTag) {
        this.startZeit = LocalTime.parse(startZeit);
        this.endZeit = LocalTime.parse(endZeit);
        this.startTag = LocalDate.parse(startTag);
        this.endTag = LocalDate.parse(endTag);
    }

    boolean liegtImPraktikumsZeitraum(LocalDateTime start, LocalDateTime ende){
        DayOfWeek dayOfWeek = start.getDayOfWeek();
        if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)){
            return false;
        } else if (start.toLocalDate().isBefore(startTag)){
            return false;
        } else if (ende.toLocalDate().isAfter(endTag)){
            return false;
        }
        return !(start.toLocalTime().isBefore(startZeit) || ende.toLocalTime().isAfter(endZeit));
    }

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
            return istAmEndeDesTages(endeZweiterUrlaub) && zeitZwischenUrlauben >= 90;
        } else if (istAmEndeDesTages(endeErsterUrlaub)) {
            Long zeitZwischenUrlauben = Duration.between(endeZweiterUrlaub, startErsterUrlaub).toMinutes();
            return istAmAnfangDesTages(startZweiterUrlaub) && zeitZwischenUrlauben >= 90;
        }
        return false;
    }

    boolean istAmEndeDesTages(LocalDateTime zeit) {
        LocalDate tag = zeit.toLocalDate();
        return zeit.equals(LocalDateTime.of(tag, endZeit));
    }

    boolean istAmAnfangDesTages(LocalDateTime zeit) {
        LocalDate tag = zeit.toLocalDate();
        return zeit.equals(LocalDateTime.of(tag, startZeit));
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

    boolean hatAusreichendRestUrlaub (Student student, LocalDateTime start, LocalDateTime ende) {
        long dauer = Duration.between(start, ende).toMinutes();
        return dauer <= student.getResturlaubInMin();
    }

}
