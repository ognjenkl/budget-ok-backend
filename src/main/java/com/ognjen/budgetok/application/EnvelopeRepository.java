package com.ognjen.budgetok.application;

import java.util.List;

public interface EnvelopeRepository {
    Envelope save(Envelope envelope);
    List<Envelope> findAll();
    Envelope findById(long id);
    void deleteById(long id);
    Envelope update(long id, Envelope envelope);
}
