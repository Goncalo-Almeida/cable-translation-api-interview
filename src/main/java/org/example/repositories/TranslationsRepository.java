package org.example.repositories;

import org.example.entities.Translation;
import org.example.entities.TranslationId;
import org.example.entities.Word;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TranslationsRepository extends CrudRepository<Translation, TranslationId> {
    List<Translation> findByFromWordAndToWord_Language_LanguageCode(Word fromWord, char[] languageCode);
}
