package org.example.services;

import org.example.dtos.TranslationDTO;

import java.util.List;

public interface TranslationService {

    void addNewTranslation(TranslationDTO newTranslationDTO);


    List<TranslationDTO> translate(String word, String wordLanguage, String translateToLanguage);
}
