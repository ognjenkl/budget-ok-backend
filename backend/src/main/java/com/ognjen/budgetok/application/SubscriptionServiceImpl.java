package com.ognjen.budgetok.application;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

  private final String bankOkApiHost;
  private final RestTemplate restTemplate;

  public SubscriptionServiceImpl(
      @Value("${bankok.api.host}") String bankOkApiHost,
      RestTemplate restTemplate) {
    this.bankOkApiHost = bankOkApiHost;
    this.restTemplate = restTemplate;
  }

  @Override
  public SubscriptionResponse calculateSubscription(SubscriptionRequest request) {
    log.info("Calculating subscription price for request price: {}", request.price());

    // Call Bank OK's subscription-discount endpoint to get discount percentage
    String url = bankOkApiHost + "/api/subscription-discount";
    log.info("Calling Bank OK API to get discount percentage from: {}", url);

    SubscriptionDiscountResponse discountResponse = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<SubscriptionDiscountResponse>() {
        }
    ).getBody();

    log.info("Received discount percentage from Bank OK: {}", discountResponse.discount());

    // Calculate final price: price - (price * discountPercentage)
    BigDecimal discountAmount = request.price().multiply(discountResponse.discount());
    BigDecimal finalPrice = request.price().subtract(discountAmount);

    log.info("Calculated subscription: originalPrice={}, discountPercentage={}, discountAmount={}, finalPrice={}",
        request.price(), discountResponse.discount(), discountAmount, finalPrice);

    return new SubscriptionResponse(finalPrice);
  }
}
