package de.hhu.propra.chicken;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class ChickenConfiguration {

    @Value("${praktikumsdauer.startZeit:LocalTime.of(9, 30)}")
    private static LocalTime STARTZEIT;

    @Value("${praktikumsdauer.endZeit:LocalTime.of(13, 30)}")
    private static LocalTime ENDZEIT;

    @Value("${praktikumsdauer.startTag:LocalDate.of(2022, 3, 7)}")
    private static LocalDate STARTTAG;

    @Value("${praktikumsdauer.endTag:LocalDate.of(2022, 3, 25)}")
    private static LocalDate ENDTAG;



    @Bean
    public StudentenService studentenServiceErstellen(StudentRepository studentRepo){
        return new StudentenService(studentRepo);
    }

    @Bean
    public KlausurService klausurServiceErstellen(KlausurRepository klausurRepo, LsfValidierung lsfValidierung){
        return new KlausurService(klausurRepo, lsfValidierung);
    }

    @Bean
    public LsfValidierung lsfValidierungErstellen(){
        return new LsfValidierung();
    }

    @Bean
    public BuchungsService buchungsServiceErstellen(StudentRepository studentRepo, KlausurRepository klausurRepo, BuchungsValidierung buchungsValidierung) throws IOException {
        return new BuchungsService(studentRepo,klausurRepo, buchungsValidierung);
    }

    @Bean
    public BuchungsValidierung buchungsValidierungErstellen(){
        return new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
    }
}
