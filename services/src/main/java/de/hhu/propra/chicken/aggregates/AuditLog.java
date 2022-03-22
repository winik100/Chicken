package de.hhu.propra.chicken.aggregates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLog {

    private final String pfad;

    public AuditLog(String pfad) throws IOException {
        this.pfad = pfad;
//        Files.createFile(Path.of(pfad));
    }

    void eintragen(String ereignis) throws IOException {
        Files.write(Path.of(pfad), Collections.singleton(ereignis + "\n"));

    }
}
