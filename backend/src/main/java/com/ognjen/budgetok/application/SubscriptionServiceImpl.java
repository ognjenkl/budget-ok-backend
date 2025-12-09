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
  private final DiscountCalculator discountCalculator;

  public SubscriptionServiceImpl(
      @Value("${bankok.api.host}") String bankOkApiHost,
      RestTemplate restTemplate,
      DiscountCalculator discountCalculator) {
    this.bankOkApiHost = bankOkApiHost;
    this.restTemplate = restTemplate;
    this.discountCalculator = discountCalculator;
  }

  @Override
  public SubscriptionResponse calculateSubscription(SubscriptionRequest request) {
    log.info("Calculating subscription price for request price: {}", request.price());

    BigDecimal discountPercentage = discountCalculator.calculateDiscount();

    log.info("Calculated discount percentage locally: {}", discountPercentage);

    BigDecimal discountAmount = request.price().multiply(discountPercentage);
    BigDecimal finalPrice = request.price().subtract(discountAmount);

    log.info("Calculated subscription: originalPrice={}, discountPercentage={}, discountAmount={}, finalPrice={}",
        request.price(), discountPercentage, discountAmount, finalPrice);

    return new SubscriptionResponse(finalPrice);
  }

  @Override
  public TaxResponse calculateTax(TaxRequest request) {
    log.info("Calculating tax for request price: {}", request.price());

    // Call Bank OK's tax endpoint to get tax rate
    String url = bankOkApiHost + "/api/tax";
    log.info("Calling Bank OK API to get tax rate from: {}", url);

    TaxRateResponse taxRateResponse = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<TaxRateResponse>() {
        }
    ).getBody();

    log.info("Received tax rate from Bank OK: {}", taxRateResponse.taxRate());

    // Calculate final price: price + (price * taxRate)
    BigDecimal taxAmount = request.price().multiply(taxRateResponse.taxRate());
    BigDecimal finalPrice = request.price().add(taxAmount);

    log.info("Calculated tax: basePrice={}, taxRate={}, taxAmount={}, finalPrice={}",
        request.price(), taxRateResponse.taxRate(), taxAmount, finalPrice);

    return new TaxResponse(finalPrice);
  }
}
