package org.example.cakeshop.controller;

import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.service.CustomerService;
import org.example.cakeshop.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final CustomerService customerService;
    private final OrderService orderService;

    public ProfileController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    //отображение профиля
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(authentication.getName());
        if (customer == null) {
            return "redirect:/login";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.getByCustomerId(customer.getId()));
        return "profile";
    }

    //обновление телефона и адреса
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }
        if (isAdmin(authentication)) {
            redirectAttributes.addFlashAttribute("success", "Для администратора контакты в профиле недоступны.");
            return "redirect:/profile";
        }
        Customer customer = customerService.findByEmail(authentication.getName());
        if (customer != null) {
            customerService.updateProfile(customer.getId(), phone, address);
            redirectAttributes.addFlashAttribute("success", "Профиль обновлен");
        }
        return "redirect:/profile";
    }

    @PostMapping("/orders/delete") //удаление заказа
    public String deleteOrder(@RequestParam Long orderId, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(authentication.getName());
        if (customer != null) {
            orderService.deleteByOwner(orderId, customer.getId());
        }
        return "redirect:/profile";
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) //в строку
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
