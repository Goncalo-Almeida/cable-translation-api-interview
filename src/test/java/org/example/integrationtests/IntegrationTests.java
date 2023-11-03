package org.example.integrationtests;

import org.example.dtos.TranslationDTO;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.stream.Stream;

@Ignore
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(value = "integration-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

//    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @ParameterizedTest
    @MethodSource
    public void addTranslations(TranslationDTO translationDTO) {

    }

    Stream<Arguments> addTranslations() {
        return Stream.of(
                Arguments.of(new TranslationDTO(
                        "Hi", "EN",
                        "Olá", "PT")
                ));
    }

    @ParameterizedTest
    @MethodSource
    public void getTranslations(String word, String wordLanguage, String translateToLanguage,
                                TranslationDTO expectedTranslation) {

    }

    Stream<Arguments> getTranslations() {
        return Stream.of(
                Arguments.of("Hi", "EN", "PT", new TranslationDTO(
                        "Hi", "EN",
                        "Olá", "PT"))
        );
    }

}
