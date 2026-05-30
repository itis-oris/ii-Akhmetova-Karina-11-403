package org.example.cakeshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public Object handleNotFound(NotFoundException ex, HttpServletRequest request, Model model) {
        log.warn("Not found: {}", ex.getMessage());
        //для api ResponseEntity 404 и телом в виде Map ключом "error"
        if (isApi(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
        //для mvc добавляет в модель status и errorMessage
        model.addAttribute("status", 404);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    //badRequest
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        log.warn("IllegalArgument: {}", ex.getMessage());
        if (isApi(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("status", 400);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    //для бизнес-ошибок
    @ExceptionHandler(AppException.class)
    public Object handleApp(AppException ex, HttpServletRequest request, Model model) {
        log.warn("AppException: {}", ex.getMessage());
        if (isApi(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("status", 400);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    //ошибки валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request, Model model) {
        log.debug("Validation failed: {}", ex.getMessage());
        if (isApi(request)) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("message", "Validation failed", "errors", errors));
        }
        model.addAttribute("status", 400);
        model.addAttribute("errorMessage", "Ошибка валидации.");
        return "error";
    }

    //ошибка сервера
    @ExceptionHandler(Exception.class)
    public Object handleAny(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled exception", ex);
        if (isApi(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Внутренняя ошибка сервера"));
        }
        model.addAttribute("status", 500);
        model.addAttribute("errorMessage", "Что-то пошло не так.");
        return "error";
    }

    //распознает апи запроса
    private boolean isApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/");
    }
}

