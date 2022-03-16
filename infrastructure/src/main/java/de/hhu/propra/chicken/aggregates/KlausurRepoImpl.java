package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KlausurRepoImpl implements KlausurRepository {

    DBKlausurRepo repo;

    public KlausurRepoImpl(DBKlausurRepo repo) {
        this.repo = repo;
    }

    @Override
    public Klausur klausurMitLsfId(LsfId id) {

        return null;
    }

    @Override
    public void save(Klausur klausur) {
        KlausurEntity klausurEntity = new KlausurEntity(klausur.getId(), klausur.getLsfId().getId(), klausur.getName(), klausur.getStart(), klausur.getEnde(), klausur.getTyp());
        repo.save(klausurEntity);
    }

    @Override
    public Set<Klausur> klausurenMitReferenzen(Set<KlausurReferenz> referenzen) {
        Set<Long> ids = referenzen.stream().map(KlausurReferenz::id).collect(Collectors.toSet());
        Iterable<KlausurEntity> klausurEntities = repo.findAllById(ids);
        HashSet<Klausur> klausuren = new HashSet<>();
        for (KlausurEntity k : klausurEntities) {
            Klausur klausur = new Klausur(new LsfId(k.lsfId()), k.name(), k.start(), k.ende(), k.typ());
            klausuren.add(klausur);
        }
        return klausuren;
    }
}
