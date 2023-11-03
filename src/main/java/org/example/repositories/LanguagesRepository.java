package org.example.repositories;

import org.example.entities.Language;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LanguagesRepository extends CrudRepository<Language, char[]> {
    Optional<Language> findByLanguageCode(char[] languageCode);
}
