package com.ognjen.budgetok;

import com.ognjen.budgetok.application.DiscountCalculator;
import com.ognjen.budgetok.application.SubscriptionRequest;
import com.ognjen.budgetok.application.SubscriptionResponse;
import com.ognjen.budgetok.application.SubscriptionServiceImpl;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

  private SubscriptionServiceImpl subscriptionService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private DiscountCalculator discountCalculator;

  @BeforeEach
  void setUp() {
    subscriptionService = new SubscriptionServiceImpl("http://localhost:8081", restTemplate, discountCalculator);
  }

  @Test
  void shouldCalculateSubscriptionWithLocalDiscount() {
    // Given
    BigDecimal originalPrice = new BigDecimal("100.00");
    BigDecimal discountPercentage = new BigDecimal("0.20");
    SubscriptionRequest request = new SubscriptionRequest(originalPrice);

    when(discountCalculator.calculateDiscount()).thenReturn(discountPercentage);

    // When
    SubscriptionResponse response = subscriptionService.calculateSubscription(request);

    // Then
    // Expected: 100.00 - (100.00 * 0.20) = 100.00 - 20.00 = 80.00
    assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("80.00"));
  }

  @Test
  void shouldCalculateSubscriptionWithTenPercentDiscount() {
    // Given
    BigDecimal originalPrice = new BigDecimal("100.00");
    BigDecimal discountPercentage = new BigDecimal("0.10");
    SubscriptionRequest request = new SubscriptionRequest(originalPrice);

    when(discountCalculator.calculateDiscount()).thenReturn(discountPercentage);

    // When
    SubscriptionResponse response = subscriptionService.calculateSubscription(request);

    // Then
    // Expected: 100.00 - (100.00 * 0.10) = 100.00 - 10.00 = 90.00
    assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("90.00"));
  }

  @Test
  void shouldCalculateSubscriptionWithDecimalPrice() {
    // Given
    BigDecimal originalPrice = new BigDecimal("99.99");
    BigDecimal discountPercentage = new BigDecimal("0.20");
    SubscriptionRequest request = new SubscriptionRequest(originalPrice);

    when(discountCalculator.calculateDiscount()).thenReturn(discountPercentage);

    // When
    SubscriptionResponse response = subscriptionService.calculateSubscription(request);

    // Then
    // Expected: 99.99 - (99.99 * 0.20) = 99.99 - 19.998 = 79.992
    assertThat(response.finalPrice()).isEqualByComparingTo(new BigDecimal("79.992"));
  }
}
