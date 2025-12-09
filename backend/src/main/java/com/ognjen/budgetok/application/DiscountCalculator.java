package com.ognjen.budgetok.application;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscountCalculator {

  public BigDecimal calculateDiscount() {
    LocalTime currentTime = LocalTime.now();
    BigDecimal discount = currentTime.isBefore(LocalTime.of(16, 0))
        ? new BigDecimal("0.20")
        : new BigDecimal("0.10");
    log.info("Subscription discount calculated at {}. Discount: {}", currentTime, discount);
    return discount;
  }
}
