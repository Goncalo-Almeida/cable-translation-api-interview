CREATE SCHEMA IF NOT EXISTS translations;

SET search_path TO translations;

CREATE TABLE translations.languages
(
    language_code VARCHAR(2)   NOT NULL,
    language_name VARCHAR(100) NOT NULL,

    PRIMARY KEY (language_code)
);

CREATE TABLE translations.words
(
    word          VARCHAR(100) NOT NULL,
    language_code VARCHAR(2)   NOT NULL,

    PRIMARY KEY (word), --TODO plus the language?
    UNIQUE (word, language_code),
    CONSTRAINT words_languages_fk FOREIGN KEY (language_code) REFERENCES languages (language_code)
);

CREATE TABLE translations.translations
(
    from_word VARCHAR(100) NOT NULL,
    to_word   VARCHAR(100) NOT NULL,

    UNIQUE (from_word, to_word),
    CONSTRAINT from_word_translation_words_fk FOREIGN KEY (from_word) REFERENCES words (word),
    CONSTRAINT to_word_translation_words_fk FOREIGN KEY (to_word) REFERENCES words (word)
);