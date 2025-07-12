package com.ognjen.budgetok.application;

import java.util.List;

public interface EnvelopeService {

  Envelope createEnvelope(Envelope envelope);

  List<Envelope> getAllEnvelopes();

  Envelope getEnvelopeById(Long id);
  
  void addItemToEnvelope(Long envelopeId, EnvelopeItem item);

  void deleteEnvelope(Long id);
}
