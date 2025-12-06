package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.BankOkProductsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/bankok")
public class BankOkApiController {

    @Value("${bankok.api.host}")
    private String bankOkApiHost;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/expenses")
    public BankOkProductsResponse getCartByUserId() {
        String url = bankOkApiHost + "/api/expenses";
      BankOkProductsResponse forObject = restTemplate.getForObject(url,
          BankOkProductsResponse.class);
      return forObject;
    }
}
