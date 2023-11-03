package org.example.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class TranslationId implements Serializable {
    private Word fromWord;
    private Word toWord;
}
