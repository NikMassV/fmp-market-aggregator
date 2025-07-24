package com.marketpulse.aggregator.producer;

import com.marketpulse.avro.MarketQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MarketQuoteProducer {
    private static final Logger log = LoggerFactory.getLogger(MarketQuoteProducer.class);
    private final KafkaTemplate<String, MarketQuote> kafkaTemplate;

    @Autowired
    public MarketQuoteProducer(KafkaTemplate<String, MarketQuote> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(MarketQuote quote) {
        kafkaTemplate.send("market-quotes", quote.getSymbol(), quote);
        log.info("Produced MarketQuote: {}", quote);
    }
}
