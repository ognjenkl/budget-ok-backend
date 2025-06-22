package com.ognjen.budgetok.application;

import java.util.List;
import java.util.Optional;

public interface EnvelopeRepository {

  void save(Envelope envelope);

  List<Envelope> findAll();

  Optional<Envelope> findById(Long id);
}
