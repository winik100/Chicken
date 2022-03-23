package de.hhu.propra.chicken;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ChickenConfiguration {

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
        return new BuchungsValidierung();
    }
}
