package com.ognjen.budgetok.application;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvelopeRepository {

  List<Envelope> findAll();

  Envelope findById(Long id);

  Envelope save(Envelope envelope);

  void deleteById(Long id);

  Envelope findByName(@NotBlank(message = "Name is required") String name);
}
