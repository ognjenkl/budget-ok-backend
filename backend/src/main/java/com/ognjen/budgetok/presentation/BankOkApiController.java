package com.ognjen.budgetok.presentation;

import com.ognjen.budgetok.application.BankExpense;
import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import com.ognjen.budgetok.application.ExpenseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BankOkApiController {

  @Value("${bankok.api.host}")
  private String bankOkApiHost;

  private final EnvelopeService envelopeService;
  private final RestTemplate restTemplate;

  @GetMapping("/expenses")
  public List<BankExpense> getExpenses() {
    log.info("Fetching expenses from Bank OK API: {}", bankOkApiHost + "/api/expenses");
    String url = bankOkApiHost + "/api/expenses";
    List<BankExpense> body = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<BankExpense>>() {
        }
    ).getBody();
    log.info("Received {} expenses from Bank OK API", body != null ? body.size() : 0);
    return body;
  }

  @PostMapping("/sync-bank-ok")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void syncBankOk() {
    log.info("Starting Bank OK sync process");
    List<BankExpense> expenses = getExpenses();

    for (BankExpense expense : expenses) {
      // Get or create the envelope with the bank expense's envelope name
      Envelope envelope = envelopeService.getByName(expense.envelopeName());
      if (envelope == null) {
        log.warn("Envelope not found for expense: {} in envelope: {}", expense.title(), expense.envelopeName());
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
      log.info("Added expense '{}' (price: {}) to envelope '{}'", expense.title(), expense.price(), expense.envelopeName());
    }
    log.info("Bank OK sync process completed successfully");
  }
}
