package com.ognjen.budgetok.application;

public interface SubscriptionService {
  SubscriptionResponse calculateSubscription(SubscriptionRequest request);

  TaxResponse calculateTax(TaxRequest request);
}
