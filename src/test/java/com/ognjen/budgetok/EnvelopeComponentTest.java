package com.ognjen.budgetok;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EnvelopeComponentTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private EnvelopeRepository envelopeRepository;

  @Test
  void shouldCreateAndPersistEnvelope() {
    // given
    Envelope envelope = new Envelope();
    envelope.setName("Envelope 1");
    envelope.setBudget(100.0);

    // when
    ResponseEntity<Envelope> response = restTemplate.postForEntity(
        "/api/envelopes", envelope, Envelope.class);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(201);

    Envelope body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getId()).isNotNull();

    Optional<Envelope> persisted = envelopeRepository.findById(body.getId());
    assertThat(persisted).isPresent();
    assertThat(persisted.get().getId()).isEqualTo(body.getId());
    assertThat(persisted.get().getName()).isEqualTo("Envelope 1");
    assertThat(persisted.get().getBudget()).isEqualTo(100.0);
  }
}
