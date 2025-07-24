package com.marketpulse.aggregator.config;

import com.marketpulse.avro.MarketQuote;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaProducerConfigTest {
    private KafkaProducerConfig config;

    @BeforeEach
    void setUp() {
        config = new KafkaProducerConfig();
        setField(config, "bootstrapServers", "localhost:9092");
        setField(config, "schemaRegistryUrl", "http://localhost:8081");
    }

    @Test
    void producerFactory_shouldReturnConfiguredFactory() {
        ProducerFactory<String, MarketQuote> factory = config.producerFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof DefaultKafkaProducerFactory);
        Map<String, Object> props = ((DefaultKafkaProducerFactory<String, MarketQuote>) factory).getConfigurationProperties();
        assertEquals("localhost:9092", props.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringSerializer.class, props.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(KafkaAvroSerializer.class, props.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("http://localhost:8081", props.get("schema.registry.url"));
    }

    @Test
    void kafkaTemplate_shouldReturnTemplateWithProducerFactory() {
        KafkaTemplate<String, MarketQuote> template = config.kafkaTemplate();
        assertNotNull(template);
        assertNotNull(template.getProducerFactory());
    }

    @Test
    void producerFactory_directCoverage() {
        config.producerFactory();
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 