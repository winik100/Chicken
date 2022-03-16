package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.UrlaubsEintrag;
import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.data.annotation.Id;

import java.util.Set;

public record StudentEntity(@Id Long id, String githubHandle, Long restUrlaub, Set<UrlaubsEintrag> urlaube, Set<KlausurReferenz> klausurAnmeldungen) {
}
