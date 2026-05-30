package org.example.cakeshop.controller;

import jakarta.validation.Valid;
import org.example.cakeshop.dto.ConfectionerRequest;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.service.ConfectionerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/confectioners")
public class AdminConfectionerController {
    private final ConfectionerService confectionerService;

    public AdminConfectionerController(ConfectionerService confectionerService) {
        this.confectionerService = confectionerService;
    }

    //список всех кондитеров
    @GetMapping
    public String list(Model model) {
        model.addAttribute("confectioners", confectionerService.getAll());
        return "admin/confectioners-list";
    }

    //показать форму создания
    @GetMapping("/new")
    public String createPage(Model model) {
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ConfectionerRequest());
        }
        model.addAttribute("isEdit", false);
        model.addAttribute("shops", confectionerService.getAllShops());
        return "admin/confectioner-form";
    }

    //создать нового кондитера
    @PostMapping
    public String create(@Valid @ModelAttribute("request") ConfectionerRequest confectionerRequest,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("shops", confectionerService.getAllShops());
            model.addAttribute("error", "Заполните имя кондитера.");
            return "admin/confectioner-form";
        }
        Confectioner created = confectionerService.create(confectionerRequest);
        redirectAttributes.addFlashAttribute("success", "Кондитер создан");
        redirectAttributes.addAttribute("pickedConfectionerId", created.getId());
        return "redirect:/admin/cakes";
    }

    //показать форму редактирования
    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        var c = confectionerService.getById(id);
        ConfectionerRequest req = new ConfectionerRequest();
        req.setName(c.getName());
        req.setPhone(c.getPhone());
        req.setShopId(c.getShop().getId());
        model.addAttribute("confectionerId", id);
        model.addAttribute("request", req);
        model.addAttribute("isEdit", true);
        model.addAttribute("shops", confectionerService.getAllShops());
        return "admin/confectioner-form";
    }

    //обновление кондитера
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("request") ConfectionerRequest confectionerRequest,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("confectionerId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("shops", confectionerService.getAllShops());
            model.addAttribute("error", "Заполните имя кондитера.");
            return "admin/confectioner-form";
        }
        confectionerService.update(id, confectionerRequest);
        redirectAttributes.addFlashAttribute("success", "Кондитер обновлён");
        return "redirect:/admin/confectioners/" + id + "/edit";
    }

    //удаление кондитера
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            confectionerService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Кондитер удалён");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/cakes";
    }
}
