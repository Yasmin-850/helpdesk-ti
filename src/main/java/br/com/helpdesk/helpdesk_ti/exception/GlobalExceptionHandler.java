package br.com.helpdesk.helpdesk_ti.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata erros lançados com ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarResponseStatusException(
            ResponseStatusException exception) {

        Map<String, Object> erro = new LinkedHashMap<>();

        erro.put("dataHora", LocalDateTime.now());
        erro.put("status", exception.getStatusCode().value());
        erro.put("erro", exception.getReason());

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(erro);
    }

    // Trata erros de validação do @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException exception) {

        Map<String, Object> campos = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        campos.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("dataHora", LocalDateTime.now());
        resposta.put("status", 400);
        resposta.put("erro", "Erro de validação");
        resposta.put("campos", campos);

        return ResponseEntity
                .badRequest()
                .body(resposta);
    }

    // Trata erros inesperados do sistema
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroGeral(
            Exception exception) {

        Map<String, Object> erro = new LinkedHashMap<>();

        erro.put("dataHora", LocalDateTime.now());
        erro.put("status", 500);
        erro.put("erro", "Erro interno do servidor");

        return ResponseEntity
                .internalServerError()
                .body(erro);
    }
}
