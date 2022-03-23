package de.hhu.propra.chicken.aggregates;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class KlausurRepoImpl implements KlausurRepository {

    DBKlausurRepo repo;

    public KlausurRepoImpl(DBKlausurRepo repo) {
        this.repo = repo;
    }

    @Override
    public Klausur klausurMitLsfId(Long lsfId) {
        Optional<KlausurEntity> klausurEntity = repo.findByLsfId(lsfId);
        KlausurEntity klausur = klausurEntity.orElse(null);
        if(klausur == null) {
            return null;
        }
        return new Klausur(klausur.getId(), klausur.getLsfId(), klausur.getName(), klausur.getStart(), klausur.getEnde(), klausur.getTyp());
    }

    @Override
    public void save(Klausur klausur) {
        KlausurEntity klausurEntity = new KlausurEntity(klausur.getLsfId(), klausur.getName(), klausur.getStart(), klausur.getEnde(), klausur.getTyp());
        repo.save(klausurEntity);
    }

    @Override
    public Set<Klausur> klausurenMitReferenzen(Set<Long> referenzen) {
        Iterable<KlausurEntity> klausurEntities = repo.findAllById(referenzen);
        HashSet<Klausur> klausuren = new HashSet<>();
        for (KlausurEntity k : klausurEntities) {
            Klausur klausur = new Klausur(k.getId(), k.getLsfId(), k.getName(), k.getStart(), k.getEnde(), k.getTyp());
            klausuren.add(klausur);
        }
        return klausuren;
    }

    @Override
    public Set<Klausur> alle() {
        Iterable<KlausurEntity> alleKlausuren = repo.findAll();
        HashSet<Klausur> klausurSet = new HashSet<>();
        alleKlausuren.forEach(k -> klausurSet.add(new Klausur(k.getId(), k.getLsfId(), k.getName(), k.getStart(), k.getEnde(), k.getTyp())));
        return klausurSet;
    }
}
