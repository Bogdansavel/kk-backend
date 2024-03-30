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
        redirectView.setUrl("https://docs.google.com/forms/d/e/1FAIpQLSc1JLQ1Oxcgy7730NoAsJrUI5JhjgXTSUOKnm2bUE-cH6Nm1Q/viewform?usp=pp_url&entry.518733161=@" + username);
        //trigger build
        return redirectView;
    }
}
