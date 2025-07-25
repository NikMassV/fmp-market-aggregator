package com.marketpulse.storage.config;

import com.marketpulse.avro.MarketQuote;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConsumerConfigTest {
    private KafkaConsumerConfig config;

    @BeforeEach
    void setUp() {
        config = new KafkaConsumerConfig();
        setField(config, "bootstrapServers", "localhost:9092");
        setField(config, "schemaRegistryUrl", "http://localhost:8081");
        setField(config, "groupId", "test-group");
    }

    @Test
    void consumerFactory_shouldReturnConfiguredFactory() {
        ConsumerFactory<String, MarketQuote> factory = config.consumerFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof DefaultKafkaConsumerFactory);
        Map<String, Object> props = ((DefaultKafkaConsumerFactory<String, MarketQuote>) factory).getConfigurationProperties();
        assertEquals("localhost:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(StringDeserializer.class, props.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(KafkaAvroDeserializer.class, props.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("http://localhost:8081", props.get("schema.registry.url"));
        assertEquals(true, props.get("specific.avro.reader"));
        assertEquals("test-group", props.get(ConsumerConfig.GROUP_ID_CONFIG));
    }

    @Test
    void kafkaListenerContainerFactory_shouldReturnFactoryWithConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MarketQuote> factory = config.kafkaListenerContainerFactory();
        assertNotNull(factory);
        assertNotNull(factory.getConsumerFactory());
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