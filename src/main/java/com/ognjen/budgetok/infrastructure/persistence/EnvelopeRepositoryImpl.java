package com.ognjen.budgetok.infrastructure.persistence;

import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeNotFoundException;
import com.ognjen.budgetok.application.EnvelopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class EnvelopeRepositoryImpl implements EnvelopeRepository {
    
    private final JdbcEnvelopeRepository jdbcRepository;

    @Override
    public Envelope save(Envelope envelope) {
        return jdbcRepository.save(envelope);
    }

    @Override
    public Envelope findById(long id) {
        return jdbcRepository.findById(id)
                .orElseThrow(() -> new EnvelopeNotFoundException(id));
    }

    @Override
    public List<Envelope> findAll() {
        return jdbcRepository.findAll();
    }

    @Override
    public void deleteById(long id) {
        if (!jdbcRepository.existsById(id)) {
            throw new EnvelopeNotFoundException(id);
        }
        jdbcRepository.deleteById(id);
    }

    @Override
    public Envelope update(long id, Envelope envelope) {
        if (!jdbcRepository.existsById(id)) {
            throw new EnvelopeNotFoundException(id);
        }
        envelope.setId(id);
        return jdbcRepository.save(envelope);
    }
}
