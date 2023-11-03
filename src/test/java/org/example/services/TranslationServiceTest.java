package org.example.services;

import org.example.dtos.TranslationDTO;
import org.example.entities.Language;
import org.example.entities.Translation;
import org.example.entities.Word;
import org.example.repositories.LanguagesRepository;
import org.example.repositories.TranslationsRepository;
import org.example.repositories.WordsRepository;
import org.example.validations.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class TranslationServiceTest {
    @MockBean
    private LanguagesRepository languagesRepository;
    @MockBean
    private WordsRepository wordsRepository;
    @MockBean
    private TranslationsRepository translationsRepository;

    @Autowired
    TranslationService translationService;

    @ParameterizedTest
    @MethodSource
    public void addNewTranslationShouldThrowValidationExceptionIfLanguagesDontExist(TranslationDTO translationDTO) {
        assertThrows(ValidationException.class, () -> translationService.addNewTranslation(translationDTO));
    }

    Stream<Arguments> addNewTranslationShouldThrowValidationExceptionIfLanguagesDontExist() {
        return Stream.of(
                Arguments.of(new TranslationDTO(
                                "Hi", "EG",
                                "Olá", "PT"),
                        Arguments.of(new TranslationDTO(
                                "Hi", "EN",
                                "Olá", "PG")
                        ),
                        Arguments.of(new TranslationDTO(
                                "Hi", "EG",
                                "Olá", "PG")
                        ))
        );
    }

    @Test
    public void addNewTranslationShouldSaveWordsAndTwoTranslations() {
        final var english = new Language("EN".toCharArray(), "English");
        final var portuguese = new Language("PT".toCharArray(), "Portuguese");
        final TranslationDTO translationDTO = new TranslationDTO(
                "Hi", "EN",
                "Olá", "PT"
        );
        final var originalWord = new Word(translationDTO.originalWord(), english);
        final var translatedWord = new Word(translationDTO.translatedWord(), portuguese);

        when(languagesRepository.findByLanguageCode("EN".toCharArray())).thenReturn(Optional.of(english));
        when(languagesRepository.findByLanguageCode("PT".toCharArray())).thenReturn(Optional.of(portuguese));
        when(wordsRepository.save(originalWord)).thenReturn(originalWord);
        when(wordsRepository.save(translatedWord)).thenReturn(translatedWord);

        translationService.addNewTranslation(translationDTO);

        verify(wordsRepository, times(2)).save(any(Word.class));
        verify(translationsRepository, times(1)).saveAll(anyList());
    }

    @ParameterizedTest
    @MethodSource
    public void translateShouldThrowValidationExceptionIfLanguagesDontExist(String word, String wordLanguage, String translateToLanguage) {
        assertThrows(ValidationException.class, () -> translationService.translate(word, wordLanguage, translateToLanguage));
    }

    Stream<Arguments> translateShouldThrowValidationExceptionIfLanguagesDontExist() {
        return Stream.of(
                Arguments.of("Hi", "EG", "PT"),
                Arguments.of("Hi", "EN", "PG"),
                Arguments.of("Hi", "EG", "PG")
        );
    }

    @Test
    public void translateShouldReturnTranslationDtos() {
        final var english = new Language("EN".toCharArray(), "English");
        final var portuguese = new Language("PT".toCharArray(), "Portuguese");

        when(languagesRepository.findByLanguageCode("EN".toCharArray())).thenReturn(Optional.of(english));
        when(languagesRepository.findByLanguageCode("PT".toCharArray())).thenReturn(Optional.of(portuguese));
        when(translationsRepository.findByFromWordAndToWord_Language_LanguageCode(any(Word.class), any(char[].class)))
                .thenReturn(List.of(
                        new Translation(
                                new Word("Hi", english),
                                new Word("Olá", portuguese)
                        )
                ));
        final var actualTranslation = translationService.translate("Hi", "EN", "PT");
        final var expectedTranslation = List.of(new TranslationDTO(
                "Hi", "EN",
                "Olá", "PT"
        ));
        assertEquals(expectedTranslation, actualTranslation);
    }

}
