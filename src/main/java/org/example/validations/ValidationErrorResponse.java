package org.example.validations;

import java.util.List;

public record ValidationErrorResponse(String traceId, List<String> errors) {
}
