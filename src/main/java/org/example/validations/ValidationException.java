package org.example.validations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {
    final Set<String> errors;
}
