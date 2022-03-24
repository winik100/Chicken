package de.hhu.propra.chicken;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class ChickenConfiguration {
    @Bean
    public StudentenService studentenServiceErstellen(StudentRepository studentRepo){
        return new StudentenService(studentRepo);
    }

    @Bean
    public KlausurService klausurServiceErstellen(KlausurRepository klausurRepo, LsfValidierung lsfValidierung, BuchungsValidierung buchungsValidierung){
        return new KlausurService(klausurRepo, lsfValidierung, buchungsValidierung);
    }

    @Bean
    public LsfValidierung lsfValidierungErstellen(){
        return new LsfValidierung();
    }

    @Bean
    public BuchungsService buchungsServiceErstellen(StudentRepository studentRepo, KlausurRepository klausurRepo, BuchungsValidierung buchungsValidierung) throws IOException {
        return new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
    }

    @Bean
    public BuchungsValidierung buchungsValidierungErstellen(@Value("${praktikumsdauer.startZeit}") String startZeit,
                                                            @Value("${praktikumsdauer.endZeit}") String endZeit,
                                                            @Value("${praktikumsdauer.startTag}") String startTag,
                                                            @Value("${praktikumsdauer.endTag}") String endTag){
        return new BuchungsValidierung(startZeit, endZeit, startTag, endTag);
    }
}
