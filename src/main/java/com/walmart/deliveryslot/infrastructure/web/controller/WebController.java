package com.walmart.deliveryslot.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Inicio");
        return "pages/home";
    }

    @GetMapping("/reservar")
    public String booking(Model model) {
        model.addAttribute("pageTitle", "Reservar despacho");
        return "pages/booking";
    }
}
