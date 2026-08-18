package com.iainturfan;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AnalysisException.class)
    public ResponseEntity<Map<String, String>> handleAnalysisException(AnalysisException exception) {
        return ResponseEntity
                .badRequest()
                .body(error(exception.getMessage(), exception.getCode()));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Map<String, String>> handleMaxUploadSize() {
        return ResponseEntity
                .badRequest()
                .body(error("El archivo supera el tamaño máximo de 10 MB", "FILE_TOO_LARGE"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleMissingPart(MissingServletRequestPartException exception) {
        String field = exception.getRequestPartName();
        return ResponseEntity
                .badRequest()
                .body(error("Falta el archivo " + field, "MISSING_FILE"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("Ha ocurrido un error inesperado", "INTERNAL_ERROR"));
    }

    private Map<String, String> error(String message, String code) {
        return Map.of(
                "message", message,
                "code", code
        );
    }
}
