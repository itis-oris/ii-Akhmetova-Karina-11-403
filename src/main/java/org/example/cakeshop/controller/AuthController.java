package org.example.cakeshop.controller;

import jakarta.validation.Valid;
import org.example.cakeshop.dto.RegisterRequest;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    //страница входа
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        //если есть ошибка ошибка в шаблон
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        //если вышел сообщение о выходе
        if (logout != null) {
            model.addAttribute("logoutOk", true);
        }
        return "login";
    }

    //страница регистрации
    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    //обработка отправки формы регистрации
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerError", "Проверьте поля.");
            return "register";
        }
        try {
            customerService.register(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Можно войти.");
            //редирект на вход
            return "redirect:/login";
        } catch (AppException ex) {
            // например, email/username уже заняты
            model.addAttribute("registerError", ex.getMessage());
            return "register";
        }
    }
}

