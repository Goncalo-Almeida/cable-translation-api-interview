package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "languages")
public class Language {

    @Id
    private char[] languageCode = new char[2];
    private String languageName;
}
