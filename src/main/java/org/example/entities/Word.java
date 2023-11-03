package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "words")
public class Word {

    @Id
    private String word;

    @ManyToOne
    @JoinColumn(name = "language_code")
    private Language language;
}
