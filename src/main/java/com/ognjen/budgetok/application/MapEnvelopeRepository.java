package com.ognjen.budgetok.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MapEnvelopeRepository implements EnvelopeRepository {

  Map<Long, Envelope> envelopes = new HashMap<>();

  @Override
  public long save(Envelope envelope) {
    if (envelope.getId() == null) {
      // New envelope - assign ID
      long newId = envelopes.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
      envelope.setId(newId);
    }
    envelopes.put(envelope.getId(), envelope);
    System.out.println("Envelope saved: " + envelope.getName() + " with budget " + envelope.getBudget() + " and id " + envelope.getId());

    return envelope.getId();
  }

  @Override
  public List<Envelope> findAll() {
    return envelopes.values().stream().toList();
  }

  @Override
  public Optional<Envelope> findById(Long id) {
    return Optional.ofNullable(envelopes.get(id));
  }

  @Override
  public void delete(Long id) {
    Envelope envelope = envelopes.remove(id);
    if (envelope != null) {
      System.out.println("Envelope deleted: " + envelope.getName() + " with id " + envelope.getId());
    } else {
      System.out.println("No envelope found with id: " + id);
    }
  }
}
