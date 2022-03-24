package de.hhu.propra.chicken.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLog {

    private final String pfad;

    public AuditLog(String pfad) {
        this.pfad = pfad;
    }

    private void eintragen(String wer, String ereignis, String ereignisTyp, LocalDateTime zeitpunkt) throws IOException {
        DateTimeFormatter zeitFormatierer = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formatiertesEreignis = zeitpunkt.format(zeitFormatierer) + ": " + "<<" + wer + ">>" + " " + ereignisTyp + ": " + ereignis + "\n";
        FileOutputStream fileOutputStream = new FileOutputStream(pfad, true);
        fileOutputStream.write(formatiertesEreignis.getBytes());
        fileOutputStream.close();
    }

    public void info(String wer, String ereignis, LocalDateTime zeitpunkt) throws IOException {
        String ereignisTyp = "INFO";
        eintragen(wer, ereignis, ereignisTyp, zeitpunkt);
    }

    public void error(String wer, String ereignis, LocalDateTime zeitpunkt) throws IOException {
        String ereignisTyp = "ERROR";
        eintragen(wer, ereignis, ereignisTyp, zeitpunkt);
    }
}
