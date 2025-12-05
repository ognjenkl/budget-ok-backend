package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.BankOkCart;
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
    public BankOkCart getCartByUserId() {
        String url = bankOkApiHost + "/api/expenses";
        return restTemplate.getForObject(url, BankOkCart.class);
    }
}
