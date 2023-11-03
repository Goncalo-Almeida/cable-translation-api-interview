package org.example.repositories;

import org.example.entities.Word;
import org.springframework.data.repository.CrudRepository;

public interface WordsRepository extends CrudRepository<Word, String> {
}
