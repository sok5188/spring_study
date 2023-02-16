package com.example.guess_music.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(@RequestParam(value = "error", required = false) String error, Model model) {
        if(error!=null){
            model.addAttribute("error",error);
        }
        return "home";
    }
}
