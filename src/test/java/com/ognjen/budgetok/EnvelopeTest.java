package com.ognjen.budgetok;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import com.ognjen.budgetok.application.EnvelopeServiceImpl;
import com.ognjen.budgetok.application.InMemoryEnvelopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvelopeTest {

  EnvelopeService service;

  @BeforeEach
  void setUp() {
    service = new EnvelopeServiceImpl(new InMemoryEnvelopeRepository());
  }

  @Test
  void shouldCreateEnvelope() {
    var envelopes = service.getAll();
    assertThat(envelopes).isEmpty();

    Envelope envelope = new Envelope();
    envelope.setName("Envelope 1");
    envelope.setBudget(100.0);

    Envelope createdEnvelope = service.create(envelope);

    assertThat(createdEnvelope.getId()).isNotNull();
    assertThat(createdEnvelope.getName()).isEqualTo("Envelope 1");
    assertThat(createdEnvelope.getBudget()).isEqualTo(100.0);

    envelopes = service.getAll();
    assertThat(envelopes).hasSize(1);
  }

}
