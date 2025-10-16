package com.chicken.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CameraController {


    @GetMapping("/coop-feed")
    public String coopFeed(Model model) {
        model.addAttribute("cameraUrl", "http://192.168.0.1:567");
        return "admin/coop-feed";
    }
}
