package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.TranslationDTO;
import org.example.services.TranslationService;
import org.example.validations.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(TranslationApiController.class)
public class TranslationApiControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TranslationService service;

    @ParameterizedTest
    @MethodSource
    public void addTranslationShouldReturnErrorListWhenValidationsFail(TranslationDTO translationDTO, List<String> errors)
            throws Exception {

        mvc.perform(put("/api/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(translationDTO))
                        .header("X-Trace-Id", "trace-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.traceId", is("trace-id")))
                .andExpect(jsonPath("$.errors", hasSize(errors.size())))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(errors.toArray())));
    }

    Stream<Arguments> addTranslationShouldReturnErrorListWhenValidationsFail() {
        return Stream.of(
                Arguments.of(new TranslationDTO(
                                null, "EN",
                                "Olá", "PT"),
                        List.of("The original word is mandatory")),

                Arguments.of(new TranslationDTO(
                                "Hi", "",
                                "Olá", "PT"),
                        List.of("The language of the original word is mandatory",
                                "Language code should have exactly two UPPERCASE letters")),

                Arguments.of(new TranslationDTO(
                                "Hi", "EN",
                                "", ""),
                        List.of("Language code should have exactly two UPPERCASE letters",
                                "The language of the translated word is mandatory",
                                "The translated word is mandatory",
                                "Words should only contain UTF-8 characters"))
        );
    }

    @Test
    public void addTranslationShouldReturnErrorListWhenLanguagesDontExist() throws Exception {
        final var translationDTO = new TranslationDTO(
                "Hi", "EG",
                "Olá", "PG");

        doThrow(new ValidationException(Set.of(
                "EG language is not supported",
                "PG language is not supported"
        ))).when(service).addNewTranslation(translationDTO);


        mvc.perform(put("/api/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(translationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors",
                        containsInAnyOrder("EG language is not supported",
                                "PG language is not supported")));
    }

    @Test
    public void addTranslationShouldAddTranslation() throws Exception {
        final var translationDTO = new TranslationDTO(
                "Hi", "EN",
                "Olá", "PT");


        mvc.perform(put("/api/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(translationDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void translateWordShouldReturnEmptyListIfNoTranslation() throws Exception {
        final var translationDTO = new TranslationDTO(
                "Hi", "EN",
                "Olá", "PT");

        mvc.perform(get("/api/translation")
                        .queryParam("word", "Hi")
                        .queryParam("wordLanguage", "EN")
                        .queryParam("translateToLanguage", "PT"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource
    public void translateWordShouldReturnErrorListWhenValidationsFail(String word, String wordLanguage, String translateToLanguage, List<String> errors)
            throws Exception {
        mvc.perform(get("/api/translation")
                        .header("X-Trace-Id", "trace-id")
                        .queryParam("word", word)
                        .queryParam("wordLanguage", wordLanguage)
                        .queryParam("translateToLanguage", translateToLanguage))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.traceId", is("trace-id")))
                .andExpect(jsonPath("$.errors", hasSize(errors.size())))
                .andExpect(jsonPath("$.errors", containsInAnyOrder(errors.toArray())));
    }

    Stream<Arguments> translateWordShouldReturnErrorListWhenValidationsFail() {
        return Stream.of(
                Arguments.of("", "EN", "PT",
                        List.of("The word to translate can't be blank")),

                Arguments.of("", "", "PT",
                        List.of("The word to translate can't be blank",
                                "The word's language can't be blank",
                                "Language code should have exactly two UPPERCASE letters")),

                Arguments.of("Hi", "EN", "pt",
                        List.of("Language code should have exactly two UPPERCASE letters")),

                Arguments.of("Hi", "EN", null,
                        List.of("The language to which the word is to be translated can't be blank"))
        );
    }

    @Test
    public void translateWordShouldReturnErrorListWhenLanguagesDontExist() {

    }

    @Test
    public void translateWordShouldReturnAvailableTranslations() {

    }

}
