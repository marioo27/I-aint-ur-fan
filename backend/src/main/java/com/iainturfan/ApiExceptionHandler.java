package com.iainturfan;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

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

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipart(MultipartException exception) {
        log.warn("Multipart error: {}", exception.getMessage());
        return ResponseEntity
                .badRequest()
                .body(error("La petición debe incluir los archivos followers y following", "INVALID_REQUEST"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMediaType() {
        return ResponseEntity
                .badRequest()
                .body(error("La petición debe ser multipart/form-data", "INVALID_REQUEST"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported() {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(error("Método no permitido. Usa POST en /api/analyzer/analyze", "METHOD_NOT_ALLOWED"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoResourceFoundException exception) {
        log.warn("Not found: {}", exception.getResourcePath());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error("Ruta no encontrada", "NOT_FOUND"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
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
