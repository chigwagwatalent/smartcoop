package com.chicken.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CameraController {

    /**
     * Simple page that embeds the coop camera feed.
     * If your camera stream path is different (e.g. /video or /mjpeg), adjust the template JS or pass a model attribute here.
     */
    @GetMapping("/coop-feed")
    public String coopFeed(Model model) {
        // Base URL of the camera; adjust if your camera exposes a specific path like "/video" or "/mjpeg"
        model.addAttribute("cameraUrl", "http://192.168.0.1:567");
        return "admin/coop-feed";
    }
}
