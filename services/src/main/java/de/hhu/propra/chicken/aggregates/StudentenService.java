package de.hhu.propra.chicken.aggregates;

import java.io.IOException;
import java.time.LocalDateTime;

public class StudentenService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    final private StudentRepository repo;

    public StudentenService(StudentRepository repo) {
        this.repo = repo;
    }

    public void studentHinzufuegen(Student student) throws IOException {
        Student studentAusDB = repo.studentMitGitHubHandle(student.getGithubHandle());
        if (studentAusDB == null) {
            repo.save(student);
            log.eintragen("Nutzerregistrierung", "Neuen Studenten für <" + student.getGithubHandle() + "> registriert.","INFO", LocalDateTime.now());
        }
    }

    public Student findeStudent(Long id) {
        return repo.studentMitId(id);
    }

    public Student findeStudentMitHandle(String gitHubHandle) {
        return repo.studentMitGitHubHandle(gitHubHandle);
    }
}
