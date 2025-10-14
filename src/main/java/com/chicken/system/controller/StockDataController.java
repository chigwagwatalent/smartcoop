package com.chicken.system.controller;

import com.chicken.system.entity.StockData;
import com.chicken.system.services.StockDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockDataController {

    private final StockDataService service;

    public StockDataController(StockDataService service) {
        this.service = service;
    }

    @GetMapping
    public String page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StockData> p = service.page(pageable);

        model.addAttribute("page", p);
        model.addAttribute("size", size);
        model.addAttribute("pageIndex", page);

        // Provide a fresh form if not coming back with validation errors
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new StockForm());
        }
        // Default: modal closed unless controller sets true (on validation errors or “Add”)
        if (!model.containsAttribute("openModal")) {
            model.addAttribute("openModal", false);
        }

        return "admin/stock";
    }

    /** Save (create or update) */
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("form") StockForm form,
            BindingResult binding,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        if (binding.hasErrors()) {
            // Keep modal open and re-render page with errors
            model.addAttribute("openModal", true);
            return page(page, size, model);
        }

        if (form.getId() == null) {
            service.create(form.getCoopId(), form.getChicksCount());
        } else {
            service.update(form.getId(), form.getCoopId(), form.getChicksCount());
        }
        return "redirect:/stock?page=" + page + "&size=" + size;
    }

    /** Delete */
    @PostMapping("/delete")
    public String delete(
            @RequestParam Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        service.delete(id);
        return "redirect:/stock?page=" + page + "&size=" + size;
    }
}
