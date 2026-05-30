package org.example.cakeshop.controller;

import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.service.CakeService;
import org.example.cakeshop.service.ConfectionerService;
import org.example.cakeshop.service.CustomerService;
import org.example.cakeshop.service.OrderService;
import org.example.cakeshop.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfectionerController {
    private final ConfectionerService confectionerService;
    private final CakeService cakeService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public ConfectionerController(ConfectionerService confectionerService,
                                  CakeService cakeService,
                                  CustomerService customerService,
                                  OrderService orderService,
                                  ReviewService reviewService) {
        this.confectionerService = confectionerService;
        this.cakeService = cakeService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }


    //
    @GetMapping("/catalog")
    public String catalog(@RequestParam(required = false) Long confectionerId, Model model) {
        if (confectionerId == null) {
            //загружаем всех кондитеров и магазины
            model.addAttribute("confectioners", confectionerService.getAll());
            model.addAttribute("shops", confectionerService.getAllShops());
            return "catalog-index";
        }
        //показывает каталог определенного кондитера
        model.addAttribute("confectioner", confectionerService.getById(confectionerId));
        model.addAttribute("cakes", cakeService.getCatalogDtosByConfectionerId(confectionerId));
        model.addAttribute("reviews", reviewService.findRecentForConfectioner(confectionerId));
        return "catalog";
    }

    // Отзыв к торту выбранного кондитера
    @PostMapping("/catalog/reviews")
    public String addCatalogReview(@RequestParam Long confectionerId,
                                   @RequestParam Long cakeId,
                                   @RequestParam int rating,
                                   @RequestParam(required = false) String comment,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(authentication.getName());
        if (customer == null) {
            return "redirect:/login";
        }
        try {
            reviewService.add(customer.getId(), confectionerId, cakeId, rating, comment);
            redirectAttributes.addFlashAttribute("success", "Отзыв сохранён.");
        } catch (AppException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/catalog?confectionerId=" + confectionerId;
    }

    //создание заказа
    @PostMapping("/orders")
    public String createOrder(@RequestParam Long confectionerId,
                              @RequestParam Long cakeId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Customer customer = customerService.findByEmail(authentication.getName());
        if (customer == null) {
            return "redirect:/login";
        }
        if (orderService.create(customer.getId(), confectionerId, cakeId) != null) {
            redirectAttributes.addFlashAttribute("success", "Заказ создан");
        } else {
            redirectAttributes.addFlashAttribute("error", "Не удалось создать заказ");
        }
        return "redirect:/catalog?confectionerId=" + confectionerId;
    }
}

