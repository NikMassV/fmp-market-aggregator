package com.marketpulse.storage.consumer;

import com.marketpulse.avro.MarketQuote;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MarketQuoteConsumer {
    private static final Logger log = LoggerFactory.getLogger(MarketQuoteConsumer.class);

    @KafkaListener(topics = "market-quotes", groupId = "market-quote-storage-group")
    public void consume(ConsumerRecord<String, MarketQuote> consumerRecord) {
        MarketQuote quote = consumerRecord.value();
        log.info("Consumed MarketQuote: {}", quote);
        // TODO: Persist to Postgres
    }
}
