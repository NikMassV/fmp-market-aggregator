package com.marketpulse.aggregator.controller;

import com.marketpulse.aggregator.producer.MarketQuoteProducer;
import com.marketpulse.avro.MarketQuote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

class MarketDataControllerInputValidationTest {
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Use a real controller with minimal dependencies for validation
        MarketQuoteProducer dummyProducer = new MarketQuoteProducer(null) {
            @Override
            public void send(MarketQuote quote) {
                // no-op
            }
        };
        WebClient.Builder builder = WebClient.builder();
        MarketDataController controller = new MarketDataController(builder, "demo", "http://localhost", dummyProducer);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    @DisplayName("Should return 400 when symbol is null or blank")
    void getQuote_nullOrBlankSymbol_returnsBadRequest() {
        webTestClient.get()
            .uri("/quotes/{symbol}", " ")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }
} 