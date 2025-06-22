package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.EnvelopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final EnvelopeService envelopeService;
    
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("envelopes", envelopeService.getAllEnvelopes());
        return "home";
    }
}
