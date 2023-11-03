package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.TranslationDTO;
import org.example.entities.Language;
import org.example.entities.Translation;
import org.example.entities.Word;
import org.example.repositories.LanguagesRepository;
import org.example.repositories.TranslationsRepository;
import org.example.repositories.WordsRepository;
import org.example.validations.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslationServiceImpl implements TranslationService {
    private final WordsRepository wordsRepository;
    private final TranslationsRepository translationsRepository;
    private final LanguagesRepository languagesRepository;

    @Override
    @Transactional
    public void addNewTranslation(TranslationDTO newTranslationDTO) {
        final var fromLang = languageFromCode(newTranslationDTO.originalWordLanguage());
        final var toLang = languageFromCode(newTranslationDTO.translatedWordLanguage());
        validateLanguages(List.of(fromLang, toLang));

        final var originalWord = wordsRepository.save(new Word(newTranslationDTO.originalWord(), fromLang));
        final var translatedWord = wordsRepository.save(new Word(newTranslationDTO.translatedWord(), toLang));

        translationsRepository.saveAll(
                List.of(
                        new Translation(originalWord, translatedWord),
                        new Translation(translatedWord, originalWord))
        );
    }

    @Override
    public List<TranslationDTO> translate(String word, String wordLanguage, String translateToLanguage) {
        final var wordLang = languageFromCode(wordLanguage);
        final var translateToLang = languageFromCode(translateToLanguage);
        validateLanguages(List.of(wordLang, translateToLang));

        final var originWord = new Word(word, wordLang);
        final var translations = translationsRepository
                .findByFromWordAndToWord_Language_LanguageCode(originWord, translateToLanguage.toCharArray());
        return mapToTranslationsDtos(translations);
    }

    private void validateLanguages(List<Language> languages) {
        final var errors = new HashSet<String>();
        for (Language lang : languages) {
            final var optLan = languagesRepository.findByLanguageCode(lang.getLanguageCode());
            if (optLan.isEmpty()) {
                errors.add(String.valueOf(lang.getLanguageCode()) + " language is not supported");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private Language languageFromCode(String languageCode) {
        return Language.builder()
                .languageCode(languageCode.toCharArray())
                .build();
    }

    private List<TranslationDTO> mapToTranslationsDtos(List<Translation> translations) {
        return translations.stream()
                .map(t -> new TranslationDTO(
                        t.getFromWord().getWord(),
                        String.valueOf(t.getFromWord().getLanguage().getLanguageCode()),
                        t.getToWord().getWord(),
                        String.valueOf(t.getToWord().getLanguage().getLanguageCode())))
                .collect(Collectors.toList());
    }
}
