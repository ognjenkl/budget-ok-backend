package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class EnvelopeWebController {

    private final EnvelopeService envelopeService;

    @GetMapping("/api/envelopes/{id}")
    public String viewEnvelope(@PathVariable Long id, Model model) {
        Envelope envelope = envelopeService.getEnvelopeById(id);
        if (envelope.getItems() == null) {
            envelope.setItems(new ArrayList<>());
        }
        model.addAttribute("envelope", envelope);
        return "envelope";
    }
}
