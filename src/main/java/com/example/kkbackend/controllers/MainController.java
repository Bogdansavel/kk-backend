package com.example.kkbackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/main")
public class MainController {
    @GetMapping
    public RedirectView getAuthRequest(@RequestParam String username) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://docs.google.com/forms/d/e/1FAIpQLSdUCvtEh9DJ-m_eBHhbv7Z-B2jH6ig5AgfF3bycG7UQ3_nwJQ/viewform?usp=pp_url&entry.518733161=@" + username);
        //trigger build
        return redirectView;
    }
}
