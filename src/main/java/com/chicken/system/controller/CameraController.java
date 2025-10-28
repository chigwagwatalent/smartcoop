package com.chicken.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CameraController {


    @GetMapping("/coop-feed")
    public String coopFeed(
            @RequestParam(name = "host", required = false) String host,
            Model model
    ) {
        final String base = (host != null && !host.isBlank())
                ? "http://" + host + ":567"
                : "http://esp32cam.local:567";  // works if mDNS is allowed on your network

        model.addAttribute("cameraUrl", base);
        return "admin/coop-feed";
    }
}
