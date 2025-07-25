package com.ognjen.budgetok.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.ognjen.budgetok.BudgetOkApplication;
import com.ognjen.budgetok.application.Envelope;
import com.ognjen.budgetok.application.EnvelopeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {BudgetOkApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Provider("Budget API")
@PactFolder("src/test/resources/pacts")
@VerificationReports
public class EnvelopeProviderTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EnvelopeService envelopeService;

    @BeforeEach
    void before(PactVerificationContext context) {
        // Reset mocks before each test
        Mockito.reset(envelopeService);
        
        // Configure the test target to point to our running application
        context.setTarget(new HttpTestTarget("localhost", port, ""));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        // Verify the interaction against the running application
        context.verifyInteraction();
    }

    @State("i have a list of envelopes")
    public void toHaveEnvelopeListState() {
        // Set up test data
        Envelope testEnvelope = new Envelope();
        testEnvelope.setId(1L);
        testEnvelope.setName("Rent");
        testEnvelope.setBudget(1200.0);
        
        // Configure mocks for the interaction
        when(envelopeService.create(any(Envelope.class))).thenReturn(testEnvelope);
        
        // Log that the state was set up
        System.out.println("Provider state 'i have a list of envelopes' set up with test envelope: " + testEnvelope);
    }
}
