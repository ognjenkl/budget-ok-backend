package com.ognjen.budgetok.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MapEnvelopeRepositoryImpl implements EnvelopeRepository {

  Map<Long, Envelope> envelopes = new HashMap<>();

  @Override
  public void save(Envelope envelope) {
    if (envelope.getId() == null) {
      // New envelope - assign ID
      long newId = envelopes.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
      envelope.setId(newId);
    }
    envelopes.put(envelope.getId(), envelope);
    System.out.println("Envelope saved: " + envelope.getName() + " with budget " + envelope.getBudget() + " and id " + envelope.getId());
  }

  @Override
  public List<Envelope> findAll() {
    return envelopes.values().stream().toList();
  }

  @Override
  public Optional<Envelope> findById(Long id) {
    return Optional.ofNullable(envelopes.get(id));
  }
}
