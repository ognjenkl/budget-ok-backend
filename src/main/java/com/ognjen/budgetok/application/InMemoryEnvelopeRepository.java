package com.ognjen.budgetok.application;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryEnvelopeRepository implements EnvelopeRepository {

    private final List<Envelope> envelopes = new ArrayList<>();
    private long nextId = 1;

    @Override
    public Envelope save(Envelope envelope) {
        if (envelope.getId() == null) {
            envelope.setId(nextId++);
            envelopes.add(envelope);
        } else {
            deleteById(envelope.getId());
            envelopes.add(envelope);
        }
        return envelope;
    }
    
    @Override
    public Envelope update(long id, Envelope envelope) {
        findById(id); // Will throw if not found
        envelope.setId(id);
        deleteById(id);
        envelopes.add(envelope);
        return envelope;
    }

    @Override
    public Envelope findById(long id) {
        return envelopes.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EnvelopeNotFoundException(id));
    }

    @Override
    public List<Envelope> findAll() {
        return new ArrayList<>(envelopes);
    }

    @Override
    public void deleteById(long id) {
        boolean removed = envelopes.removeIf(e -> e.getId().equals(id));
        if (!removed) {
            throw new EnvelopeNotFoundException(id);
        }
    }

}
