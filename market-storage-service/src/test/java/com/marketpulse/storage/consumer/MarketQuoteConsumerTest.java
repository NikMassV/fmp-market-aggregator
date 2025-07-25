package com.marketpulse.storage.consumer;

import com.marketpulse.avro.MarketQuote;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MarketQuoteConsumerTest {
    @InjectMocks
    private MarketQuoteConsumer consumer;

    @Test
    void consume_shouldProcessQuote() {
        MarketQuote quote = MarketQuote.newBuilder().setSymbol("AAPL").setPrice(150.0).build();
        ConsumerRecord<String, MarketQuote> consumerRecord = new ConsumerRecord<>("market-quotes", 0, 0L, "AAPL", quote);
        consumer.consume(consumerRecord);
        assertEquals(quote, consumerRecord.value(), "The consumed MarketQuote should match the input quote");
    }
} 