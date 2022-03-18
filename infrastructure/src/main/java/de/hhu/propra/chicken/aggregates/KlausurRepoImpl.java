package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class KlausurRepoImpl implements KlausurRepository {

    DBKlausurRepo repo;

    public KlausurRepoImpl(DBKlausurRepo repo) {
        this.repo = repo;
    }

    @Override
    public Klausur klausurMitLsfId(LsfId id) {
        Optional<KlausurEntity> klausurEntity = repo.findByLsfId(id.getId());
        KlausurEntity klausur = klausurEntity.orElse(null);
        if(klausur == null) {
            return null;
        }
        return new Klausur(klausur.id(), klausur.lsfId(), klausur.name(), klausur.start(), klausur.ende(), klausur.typ());
    }

    @Override
    public void save(Klausur klausur) {
        KlausurEntity klausurEntity = new KlausurEntity(klausur.getId(), klausur.getLsfId().getId(), klausur.getName(), klausur.getStart(), klausur.getEnde(), klausur.getTyp());
        repo.save(klausurEntity);
    }

    @Override
    public Set<Klausur> klausurenMitReferenzen(Set<Long> referenzen) {
        Iterable<KlausurEntity> klausurEntities = repo.findAllById(referenzen);
        HashSet<Klausur> klausuren = new HashSet<>();
        for (KlausurEntity k : klausurEntities) {
            Klausur klausur = new Klausur(new LsfId(k.lsfId()), k.name(), k.start(), k.ende(), k.typ());
            klausuren.add(klausur);
        }
        return klausuren;
    }
}
