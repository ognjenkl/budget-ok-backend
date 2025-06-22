package com.ognjen.budgetok.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvelopeServiceImpl implements EnvelopeService {

  private final EnvelopeRepository repository;

  @Override
  public Envelope createEnvelope(Envelope envelope) {
    repository.save(envelope);
    return envelope;
  }

  @Override
  public List<Envelope> getAllEnvelopes() {
    return repository.findAll();
  }

  @Override
  public Envelope getEnvelopeById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Envelope not found with id: " + id));
  }

  @Override
  public void addItemToEnvelope(Long envelopeId, EnvelopeItem item) {
    Envelope envelope = getEnvelopeById(envelopeId);
    if (envelope.getItems() == null) {
      envelope.setItems(new ArrayList<>());
    }
    envelope.getItems().add(item);
    repository.save(envelope);
  }
}
