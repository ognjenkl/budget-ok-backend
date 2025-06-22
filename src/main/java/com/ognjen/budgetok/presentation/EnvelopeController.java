package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class EnvelopeController {

  private final EnvelopeService service;

  @PostMapping("/api/envelopes")
  public RedirectView createEnvelope() {
    Envelope envelope = Envelope.builder()
        .name("New Envelope")
        .budget(100.0)
        .build();
        
    Envelope createdEnvelope = service.createEnvelope(envelope);
    System.out.println("Envelope created successfully with id: " + createdEnvelope.getId());

    // Redirect back to the home page
    return new RedirectView("/home");
  }
}
