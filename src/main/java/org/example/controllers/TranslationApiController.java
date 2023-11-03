package org.example.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.TranslationDTO;
import org.example.validations.ValidationErrorResponse;
import org.example.services.TranslationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/translation")
@RequiredArgsConstructor
@Validated
public class TranslationApiController {
    private final TranslationService translationService;

    @Operation(summary = "Add a new translation",
            description = "This adds a new translation for a word/expression from one language to another.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added a new translation"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    })
    })
    @PutMapping
    public void addNewTranslation(@Valid @RequestBody TranslationDTO newTranslationDTO) {
        translationService.addNewTranslation(newTranslationDTO);
    }

    @Operation(summary = "Translate a word to another language",
            description = "This translates a word/expression from one language to another.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added a new translation",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = TranslationDTO.class))
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))
                    })
    })
    @GetMapping
    public List<TranslationDTO> translateWord(@Valid
                                              @NotBlank(message = "The word to translate can't be blank") String word,

                                              @Valid
                                              @NotBlank(message = "The word's language can't be blank")
                                              @Pattern(regexp = "^[A-Z]{2}",
                                                      message = "Language code should have exactly two UPPERCASE letters") String wordLanguage,

                                              @Valid
                                              @NotBlank(message = "The language to which the word is to be translated can't be blank")
                                              @Pattern(regexp = "^[A-Z]{2}",
                                                      message = "Language code should have exactly two UPPERCASE letters") String translateToLanguage) {
        return translationService.translate(word, wordLanguage, translateToLanguage);
    }
}
