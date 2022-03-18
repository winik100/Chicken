package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Repository
public class StudentRepoImpl implements StudentRepository {

    DBStudentRepo studentRepo;
    DBKlausurRepo klausurRepo;

    public StudentRepoImpl(DBStudentRepo sRepo, DBKlausurRepo kRepo) {
        this.studentRepo = sRepo;
        this.klausurRepo = kRepo;
    }

    @Override
    public Student studentMitId(Long id) {
        Optional<StudentEntity> studentEntity = studentRepo.findById(id);
        StudentEntity student = studentEntity.orElse(null);
        if(student == null) {
            return null;
        }
        return new Student(student.id(), student.githubHandle(), student.restUrlaub(), Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public void save(Student student) {
        StudentEntity studentEntity = new StudentEntity(student.getId(), student.getGithubHandle(), student.getResturlaubInMin(), Collections.emptySet());
        studentRepo.save(studentEntity);
    }


    //TODO: Sets richtig übergeben mit Query an urlaubs_eintrag
    @Override
    public Student studentMitGitHubHandle(String gitHubHandle) {
        Optional<StudentEntity> studentEntity = studentRepo.findByGitHubHandle(gitHubHandle);
        StudentEntity student = studentEntity.orElse(null);
        if(student == null) {
            return null;
        }
        Set<UrlaubsEintragEntity> urlaubsDaten = studentRepo.findUrlaubByStudentId(student.id());
        Set<KlausurReferenz> klausurIds = klausurRepo.findKlausurIdsByStudentId(student.id());
        return new Student(student.id(), student.githubHandle(), student.restUrlaub(), Collections.emptySet(), klausurIds);
    }
}
