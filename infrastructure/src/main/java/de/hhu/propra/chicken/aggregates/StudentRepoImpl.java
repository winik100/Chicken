package de.hhu.propra.chicken.aggregates;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        StudentEntity studentEntity = new StudentEntity(null, student.getGithubHandle(), student.getResturlaubInMin(),
                student.getKlausurAnmeldungen().stream().map(KlausurReferenz::new).collect(Collectors.toSet()),
                student.getUrlaube().stream().map(x -> new UrlaubsEintragEntity(x.start(), x.ende())).collect(Collectors.toSet()));
        studentRepo.save(studentEntity);
    }



    @Override
    public Student studentMitGitHubHandle(String gitHubHandle) {
        Optional<StudentEntity> studentEntity = studentRepo.findByGithubHandle(gitHubHandle);
        StudentEntity student = studentEntity.orElse(null);
        if(student == null) {
            return null;
        }
        Set<UrlaubsEintragEntity> urlaubsEintragEntities = student.urlaubsEintraege();
        Set<UrlaubsEintrag> urlaubsEintraege = urlaubsEintragEntities.stream().map(x -> new UrlaubsEintrag(x.start(), x.ende())).collect(Collectors.toSet());
        Set<KlausurEntity> klausurEntities = klausurRepo.findAllByStudentId(student.id());
        Set<KlausurReferenz> ids = klausurEntities.stream().map(x -> new KlausurReferenz(x.getId())).collect(Collectors.toSet());
        return new Student(student.id(), student.githubHandle(), student.restUrlaub(), urlaubsEintraege, ids);
    }
}
