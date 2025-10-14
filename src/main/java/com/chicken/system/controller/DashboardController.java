package com.chicken.system.controller;

import com.chicken.system.services.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/dashboard"})
    public String dashboard(Model model) {
        var summary = dashboardService.getSummary();
        model.addAttribute("summary", summary);
        return "admin/dashboard";
    }
}
