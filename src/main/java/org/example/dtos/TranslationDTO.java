package org.example.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Validated
public record TranslationDTO(
        @NotBlank(message = "The original word is mandatory")
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Words should only contain UTF-8 characters")
        String originalWord,

        @NotBlank(message = "The language of the original word is mandatory")
        @Pattern(regexp = "^[A-Z]{2}", message = "Language code should have exactly two UPPERCASE letters")
        String originalWordLanguage,

        @NotBlank(message = "The translated word is mandatory")
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Words should only contain UTF-8 characters")
        String translatedWord,

        @NotBlank(message = "The language of the translated word is mandatory")
        @Pattern(regexp = "^[A-Z]{2}", message = "Language code should have exactly two UPPERCASE letters")
        String translatedWordLanguage
) {
}
