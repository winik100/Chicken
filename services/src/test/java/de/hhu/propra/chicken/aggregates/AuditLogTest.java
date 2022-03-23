package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditLogTest {

    @BeforeEach
    void testLogLeeren() throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("src/test/resources/testlog.txt", false);
        } catch (FileNotFoundException ignored) {}
        if (fileOutputStream != null) {
            fileOutputStream.write("".getBytes());
            fileOutputStream.close();
        }
    }

    @AfterAll
    static void logLoeschen(){
        File file = new File("src/test/resources/testlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Das Ereignis wird korrekt formatiert in die Testlog-Datei geschrieben.")
    void test_1() throws IOException {
        String pfad = "src/test/resources/testlog.txt";
        AuditLog testLog = new AuditLog(pfad);

        testLog.eintragen("testUser", "testEreignis", "INFO", LocalDateTime.of(2022,3,10, 10,0));

        Scanner scanner = new Scanner(Path.of(pfad));
        String logLine = scanner.nextLine();
        assertThat(logLine).isEqualTo("10.03.2022 10:00: <<testUser>> INFO: testEreignis");
    }
}