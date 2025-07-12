package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeItem;
import com.ognjen.budgetok.application.EnvelopeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/api/envelopes")
@RequiredArgsConstructor
public class EnvelopeController {

  private final EnvelopeService service;

//  @PostMapping
//  public RedirectView createEnvelope() {
//    Envelope envelope = Envelope.builder()
//        .name("New Envelope")
//        .budget(100.0)
//        .build();
//
//    Envelope createdEnvelope = service.createEnvelope(envelope);
//    System.out.println("Envelope created successfully with id: " + createdEnvelope.getId());
//
//    return new RedirectView("/home");
//  }

  @PostMapping
  public ResponseEntity<Envelope> createEnvelope(@RequestBody Envelope envelope) {
    Envelope createdEnvelope = service.createEnvelope(envelope);
    return ResponseEntity
        .status(201)
        .body(createdEnvelope);
  }

//  @GetMapping("/{id}")
//  public String viewEnvelope(@PathVariable Long id, Model model) {
//    Envelope envelope = service.getEnvelopeById(id);
//    if (envelope.getItems() == null) {
//      envelope.setItems(new ArrayList<>());
//    }
//    model.addAttribute("envelope", envelope);
//    return "envelope";
//  }

  @GetMapping("/{envelopeId}/items/new")
  public String showAddItemForm(@PathVariable Long envelopeId, Model model) {

    model.addAttribute("envelopeId", envelopeId);
    model.addAttribute("item", EnvelopeItem.builder()
        .date(LocalDate.now())
        .build());

    return "add-item";
  }

  @PostMapping("/{envelopeId}/items")
  public RedirectView addItem(@PathVariable Long envelopeId,
      @ModelAttribute("item") EnvelopeItem item) {

    service.addItemToEnvelope(envelopeId, item);

    return new RedirectView("/api/envelopes/" + envelopeId);
  }

  @GetMapping
  public ResponseEntity<List<Envelope>> getAllEnvelopes() {
    List<Envelope> envelopes = service.getAllEnvelopes();
    return ResponseEntity.ok(envelopes);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Envelope> getEnvelopeById(@PathVariable Long id) {
    Envelope envelope = service.getEnvelopeById(id);
    return ResponseEntity.ok(envelope);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEnvelope(@PathVariable Long id) {
    service.deleteEnvelope(id);
    return ResponseEntity.noContent().build();
  }
}
