package com.ognjen.budgetok;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeRepository;
import java.util.List;
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

  @Test
  void shouldRetrieveEnvelopes() {
    // given
    Envelope envelope = new Envelope();
    envelope.setName("Envelope 2");
    envelope.setBudget(200.0);
    envelopeRepository.save(envelope);

    // when
    ResponseEntity<List<Envelope>> response = restTemplate.exchange(
        "/api/envelopes",
        org.springframework.http.HttpMethod.GET,
        null,
        new org.springframework.core.ParameterizedTypeReference<List<Envelope>>() {
        });

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(200);

    List<Envelope> envelopes = response.getBody();
    assertThat(envelopes)
        .isNotNull()
        .isNotEmpty()
        .anyMatch(e -> e.getName().equals("Envelope 2") && e.getBudget() == 200.0);
  }

  @Test
  void shouldRetrieveEnvelopeById() {
    // given
    Envelope envelope = new Envelope();
    envelope.setName("Envelope 3");
    envelope.setBudget(300.0);
    Long savedEnvelopeId = envelopeRepository.save(envelope);

    // when
    ResponseEntity<Envelope> response = restTemplate.getForEntity(
        "/api/envelopes/" + savedEnvelopeId, Envelope.class);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(200);

    Envelope body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getId()).isEqualTo(savedEnvelopeId);
    assertThat(body.getName()).isEqualTo("Envelope 3");
    assertThat(body.getBudget()).isEqualTo(300.0);
  }

  @Test
  void shouldDeleteEnvelopeById() {
    // given
    Envelope envelope = new Envelope();
    envelope.setName("Envelope 4");
    envelope.setBudget(400.0);
    Long savedEnvelopeId = envelopeRepository.save(envelope);

    // when
    restTemplate.delete("/api/envelopes/" + savedEnvelopeId);

    // then
    Optional<Envelope> persisted = envelopeRepository.findById(savedEnvelopeId);
    assertThat(persisted).isEmpty();
  }
}
