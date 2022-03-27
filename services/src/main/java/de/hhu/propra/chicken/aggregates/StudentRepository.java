package de.hhu.propra.chicken.aggregates;

public interface StudentRepository {

    void save(Student student);

    Student studentMitGitHubHandle(String gitHubHandle);
}
