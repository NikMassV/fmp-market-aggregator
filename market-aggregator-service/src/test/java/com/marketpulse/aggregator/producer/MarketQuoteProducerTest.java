package com.marketpulse.aggregator.producer;

import com.marketpulse.avro.MarketQuote;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MarketQuoteProducerTest {
    @Mock
    private KafkaTemplate<String, MarketQuote> kafkaTemplate;

    @InjectMocks
    private MarketQuoteProducer producer;

    @Test
    void send_shouldCallKafkaTemplate() {
        MarketQuote quote = MarketQuote.newBuilder().setSymbol("AAPL").setPrice(150.0).build();
        producer.send(quote);
        verify(kafkaTemplate).send("market-quotes", "AAPL", quote);
    }
} 