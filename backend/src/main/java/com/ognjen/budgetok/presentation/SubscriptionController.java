package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.SubscriptionRequest;
import com.ognjen.budgetok.application.SubscriptionResponse;
import com.ognjen.budgetok.application.SubscriptionService;
import com.ognjen.budgetok.application.TaxRequest;
import com.ognjen.budgetok.application.TaxResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping("/calculate-discount")
  public SubscriptionResponse calculateSubscription(@RequestBody SubscriptionRequest request) {
    log.info("POST /api/subscription/calculate-discount received with price: {}", request.price());
    SubscriptionResponse response = subscriptionService.calculateSubscription(request);
    log.info("Returning subscription response with finalPrice: {}", response.finalPrice());
    return response;
  }

  @PostMapping("/calculate-tax")
  public TaxResponse calculateTax(@RequestBody TaxRequest request) {
    log.info("POST /api/subscription/calculate-tax received with price: {}", request.price());
    TaxResponse response = subscriptionService.calculateTax(request);
    log.info("Returning tax response with finalPrice: {}", response.finalPrice());
    return response;
  }
}
