package com.ognjen.budgetok.application;

import java.util.List;
import java.util.Optional;

public interface EnvelopeRepository {

  long save(Envelope envelope);

  List<Envelope> findAll();

  Optional<Envelope> findById(Long id);

  void delete(Long id);
}
