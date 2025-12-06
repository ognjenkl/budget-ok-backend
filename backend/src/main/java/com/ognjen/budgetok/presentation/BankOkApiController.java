package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.BankExpense;
import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import com.ognjen.budgetok.application.ExpenseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/bankok")
@RequiredArgsConstructor
public class BankOkApiController {

  @Value("${bankok.api.host}")
  private String bankOkApiHost;

  private final EnvelopeService envelopeService;
  private final RestTemplate restTemplate = new RestTemplate();

  @GetMapping("/expenses")
  public List<BankExpense> getExpenses() {
    String url = bankOkApiHost + "/api/expenses";
    List<BankExpense> body = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<BankExpense>>() {
        }
    ).getBody();
    return body;
  }

  @PostMapping("/sync-bank-ok")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void syncBankOk() {
    List<BankExpense> expenses = getExpenses();

    for (BankExpense expense : expenses) {
      // Get or create the envelope with the bank expense's envelope name
      Envelope envelope = envelopeService.getByName(expense.envelopeName());
      if (envelope == null) {
        continue;
      }

      // Add the bank expense as a withdrawal to the envelope
      ExpenseDto expenseDto = new ExpenseDto(
          expense.price(),
          expense.title(),
          "WITHDRAW",
          expense.id()
      );
      envelopeService.addExpense(envelope.getId(), expenseDto);
    }
  }
}
