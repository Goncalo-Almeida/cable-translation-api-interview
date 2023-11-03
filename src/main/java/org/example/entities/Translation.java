package org.example.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@IdClass(TranslationId.class)
@Table(name = "translations")
public class Translation {

    @Id
    @ManyToOne
    @JoinColumn(name = "from_word")
    private Word fromWord;

    @ManyToOne
    @Id
    @JoinColumn(name = "to_word")
    private Word toWord;
}
