package com.ognjen.budgetok;

import com.ognjen.budgetok.application.Envelope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class EnvelopeComponentTest {

    private static final Logger log = LoggerFactory.getLogger(EnvelopeComponentTest.class);

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withLogConsumer(new Slf4jLogConsumer(log));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void setUp() {
        var envelopes = restTemplate.getForEntity("/api/envelopes", Envelope[].class).getBody();
        if (envelopes != null) {
            for (var envelope : envelopes) {
                restTemplate.delete("/api/envelopes/" + envelope.getId());
            }
        }
    }

    @Test
    void testCreateAndGetEnvelope() {
        var envelope = new Envelope();
        envelope.setName("Groceries");
        envelope.setBudget(500.0);

        var response = restTemplate.postForEntity("/api/envelopes", envelope, Envelope.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        var createdEnvelope = response.getBody();
        assertThat(createdEnvelope).isNotNull();
        assertThat(createdEnvelope.getId()).isNotNull();
        assertThat(createdEnvelope.getName()).isEqualTo("Groceries");
        assertThat(createdEnvelope.getBudget()).isEqualTo(500.0);

        var getResponse = restTemplate.getForEntity("/api/envelopes/" + createdEnvelope.getId(), Envelope.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isEqualTo(createdEnvelope);
    }

    @Test
    void testRetrieveAllEnvelopes() {
        var envelope1 = new Envelope();
        envelope1.setName("Envelope 1");
        envelope1.setBudget(100.00);
        
        var envelope2 = new Envelope();
        envelope2.setName("Envelope 2");
        envelope2.setBudget(200.00);
        
        restTemplate.postForEntity("/api/envelopes", envelope1, Envelope.class);
        restTemplate.postForEntity("/api/envelopes", envelope2, Envelope.class);

        var response = restTemplate.getForEntity("/api/envelopes", Envelope[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var envelopes = response.getBody();
        assertThat(envelopes).hasSize(2);
        assertThat(envelopes).extracting("name")
                .containsExactlyInAnyOrder("Envelope 1", "Envelope 2");
    }

    @Test
    void testUpdateEnvelope() {
        var envelope = new Envelope();
        envelope.setName("Groceries");
        envelope.setBudget(500.0);

        var createdEnvelope = restTemplate.postForEntity("/api/envelopes", envelope, Envelope.class).getBody();
        assertThat(createdEnvelope).isNotNull();

        createdEnvelope.setName("Updated Groceries");
        createdEnvelope.setBudget(600.0);

        restTemplate.put("/api/envelopes/" + createdEnvelope.getId(), createdEnvelope);

        var updatedEnvelope = restTemplate.getForEntity("/api/envelopes/" + createdEnvelope.getId(), Envelope.class).getBody();
        assertThat(updatedEnvelope).isNotNull();
        assertThat(updatedEnvelope.getName()).isEqualTo("Updated Groceries");
        assertThat(updatedEnvelope.getBudget()).isEqualTo(600.0);
    }

    @Test
    void testDeleteEnvelope() {
        var envelope = new Envelope();
        envelope.setName("Groceries");
        envelope.setBudget(500.0);

        var createdEnvelope = restTemplate.postForEntity("/api/envelopes", envelope, Envelope.class).getBody();
        assertThat(createdEnvelope).isNotNull();

        restTemplate.delete("/api/envelopes/" + createdEnvelope.getId());

        var response = restTemplate.getForEntity("/api/envelopes/" + createdEnvelope.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
