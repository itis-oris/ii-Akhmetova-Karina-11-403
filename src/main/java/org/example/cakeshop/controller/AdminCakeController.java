package org.example.cakeshop.controller;

import jakarta.validation.Valid;
import org.example.cakeshop.dto.CakeRequest;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.service.CakeService;
import org.example.cakeshop.service.ConfectionerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/cakes")
public class AdminCakeController {
    private final CakeService cakeService;
    private final ConfectionerService confectionerService;

    public AdminCakeController(CakeService cakeService,
                               ConfectionerService confectionerService) {
        this.cakeService = cakeService;
        this.confectionerService = confectionerService;
    }

    //отображение списка тортов с фильтром по кондитеру
    @GetMapping
    public String list(@RequestParam(required = false) Long pickedConfectionerId, Model model) {
        List<Confectioner> confectioners = confectionerService.getAll();
        model.addAttribute("confectioners", confectioners);
        model.addAttribute("pickedConfectionerId", pickedConfectionerId);

        //ищем кондитера с выбранным айди
        var pickedMaster = confectioners.stream()
                .filter(c -> pickedConfectionerId != null && c.getId().equals(pickedConfectionerId))
                .findFirst();
        //если найден
        if (pickedMaster.isPresent()) {
            model.addAttribute("cakes", cakeService.getCatalogDtosByConfectionerId(pickedConfectionerId)); //загружаем дто его тортов
            model.addAttribute("pickedConfectionerName", pickedMaster.get().getName()); //имя кондитера
        } else {
            model.addAttribute("cakes", Collections.emptyList()); //пустой список тортов
            if (pickedConfectionerId != null) {
                model.addAttribute("pickerError", "Кондитер не найден.");
            }
        }
        return "admin/cakes-list";
    }

    //форма создания нового торта
    @GetMapping("/new")
    public String createPage(Model model) {
        model.addAttribute("cakeRequest", new CakeRequest());
        addCakeFormLists(model);
        model.addAttribute("isEdit", false);
        return "admin/cake-form";
    }

    //обработка отправки формы создания
    @PostMapping
    public String create(@Valid @ModelAttribute("cakeRequest") CakeRequest cakeRequest,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) { //если ошибки валидации
            populateCakeFormDefaults(model, false, null);
            model.addAttribute("error", "Заполните все поля.");
            return "admin/cake-form";
        }
        //создаем торт через сервис
        try {
            cakeService.create(cakeRequest);
        } catch (AppException ex) {
            populateCakeFormDefaults(model, false, null);
            model.addAttribute("error", ex.getMessage());
            return "admin/cake-form";
        }
        //после создания
        redirectAttributes.addFlashAttribute("success", "Торт создан");
        redirectAttributes.addAttribute("pickedConfectionerId", cakeRequest.getConfectionerId()); //откроет того же кондитера
        return "redirect:/admin/cakes";
    }

    //форма редактирования торта
    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        //получаем из сервиса
        var cake = cakeService.getById(id);
        //создаем новый обьект
        CakeRequest req = new CakeRequest();
        //копируем поля
        req.setName(cake.getName());
        req.setPrice(cake.getPrice());
        req.setSku(cake.getSku());
        req.setCategory(cake.getCategory());
        if (cake.getNetWeightKg() != null) {
            req.setNetWeightG((int) Math.round(cake.getNetWeightKg() * 1000.0));
        }
        if (!cake.getConfectioners().isEmpty()) {
            req.setConfectionerId(cake.getConfectioners().get(0).getId());
        }
        model.addAttribute("cakeId", id);
        model.addAttribute("cakeRequest", req);
        model.addAttribute("cakeAverageRating", cakeService.averageRating(id));
        model.addAttribute("priceInputValue", formatNumberForHtmlInput(cake.getPrice()));
        model.addAttribute("netWeightInputValue", cake.getNetWeightKg() == null
                ? ""
                : String.valueOf((int) Math.round(cake.getNetWeightKg() * 1000.0)));
        addCakeFormLists(model);
        model.addAttribute("isEdit", true);
        return "admin/cake-form";
    }

    //обработка отправки формы редактирования
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("cakeRequest") CakeRequest cakeRequest,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        //при ошибке валидации
        if (bindingResult.hasErrors()) {
            populateCakeFormDefaults(model, true, id);
            return "admin/cake-form";
        }
        try {
            cakeService.update(id, cakeRequest);
        } catch (AppException ex) {
            populateCakeFormDefaults(model, true, id);
            model.addAttribute("error", ex.getMessage());
            return "admin/cake-form";
        }
        redirectAttributes.addFlashAttribute("success", "Торт обновлён");
        redirectAttributes.addAttribute("pickedConfectionerId", cakeRequest.getConfectionerId());
        return "redirect:/admin/cakes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var cake = cakeService.getById(id);
        Long picked = cake.getConfectioners().isEmpty() ? null : cake.getConfectioners().get(0).getId();
        cakeService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Торт удалён");
        if (picked != null) {
            redirectAttributes.addAttribute("pickedConfectionerId", picked); //остаемся на том же кондитере
        }
        return "redirect:/admin/cakes";
    }


    private void populateCakeFormDefaults(Model model, boolean isEdit, Long cakeId) {
        addCakeFormLists(model);
        model.addAttribute("isEdit", isEdit);
        if (cakeId != null) {
            model.addAttribute("cakeId", cakeId);
            model.addAttribute("cakeAverageRating", cakeService.averageRating(cakeId));
        }
    }

    //все кондитеры
    private void addCakeFormLists(Model model) {
        model.addAttribute("confectioners", confectionerService.getAll());
    }

    private static String formatNumberForHtmlInput(Double value) {
        if (value == null) {
            return "";
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }
}
