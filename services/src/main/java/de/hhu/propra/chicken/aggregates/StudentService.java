package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.AuditLog;

import java.io.IOException;
import java.time.LocalDateTime;

public class StudentService {

    private final AuditLog log = new AuditLog("auditlog.txt");

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public void studentHinzufuegen(Student student) throws IOException {
        Student studentAusDB = repo.studentMitGitHubHandle(student.getGithubHandle());
        if (studentAusDB == null) {
            repo.save(student);
            log.info("Nutzerregistrierung", "Neuen Studenten für <" + student.getGithubHandle()
                    + "> registriert.", LocalDateTime.now());
        }
    }

    public Student findeStudent(Long id) {
        return repo.studentMitId(id);
    }

    public Student findeStudentMitHandle(String gitHubHandle) {
        return repo.studentMitGitHubHandle(gitHubHandle);
    }
}
