package com.marketpulse.aggregator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Quote DTO Tests")
class QuoteDtoTest {

    private static final String VALID_SYMBOL = "AAPL";
    private static final double VALID_PRICE = 150.0;

    @Test
    @DisplayName("Should create QuoteDto with valid parameters")
    void shouldCreateQuoteDtoWithValidParameters() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, VALID_PRICE);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create QuoteDto with null symbol")
    void shouldCreateQuoteDtoWithNullSymbol() {
        QuoteDto quote = new QuoteDto(null, VALID_PRICE);

        assertThat(quote.getSymbol()).isNull();
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create QuoteDto with zero price")
    void shouldCreateQuoteDtoWithZeroPrice() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, 0.0);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
        assertThat(quote.getPrice()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should create QuoteDto with negative price")
    void shouldCreateQuoteDtoWithNegativePrice() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, -10.0);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
        assertThat(quote.getPrice()).isEqualTo(-10.0);
    }

    @Test
    @DisplayName("Should create QuoteDto with maximum double price")
    void shouldCreateQuoteDtoWithMaxPrice() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, Double.MAX_VALUE);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
        assertThat(quote.getPrice()).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    @DisplayName("Should create QuoteDto with minimum double price")
    void shouldCreateQuoteDtoWithMinPrice() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, Double.MIN_VALUE);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
        assertThat(quote.getPrice()).isEqualTo(Double.MIN_VALUE);
    }

    @Test
    @DisplayName("Should create QuoteDto with empty symbol")
    void shouldCreateQuoteDtoWithEmptySymbol() {
        QuoteDto quote = new QuoteDto("", VALID_PRICE);

        assertThat(quote.getSymbol()).isEmpty();
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create QuoteDto with blank symbol")
    void shouldCreateQuoteDtoWithBlankSymbol() {
        QuoteDto quote = new QuoteDto("   ", VALID_PRICE);

        assertThat(quote.getSymbol()).isEqualTo("   ");
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create QuoteDto with long symbol")
    void shouldCreateQuoteDtoWithLongSymbol() {
        String longSymbol = "A".repeat(100);
        QuoteDto quote = new QuoteDto(longSymbol, VALID_PRICE);

        assertThat(quote.getSymbol()).isEqualTo(longSymbol);
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create QuoteDto with special characters in symbol")
    void shouldCreateQuoteDtoWithSpecialCharacters() {
        String symbolWithSpecialChars = "AAPL@#$%";
        QuoteDto quote = new QuoteDto(symbolWithSpecialChars, VALID_PRICE);

        assertThat(quote.getSymbol()).isEqualTo(symbolWithSpecialChars);
        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should set and get symbol correctly")
    void shouldSetAndGetSymbolCorrectly() {
        QuoteDto quote = new QuoteDto();
        quote.setSymbol(VALID_SYMBOL);

        assertThat(quote.getSymbol()).isEqualTo(VALID_SYMBOL);
    }

    @Test
    @DisplayName("Should set and get price correctly")
    void shouldSetAndGetPriceCorrectly() {
        QuoteDto quote = new QuoteDto();
        quote.setPrice(VALID_PRICE);

        assertThat(quote.getPrice()).isEqualTo(VALID_PRICE);
    }

    @Test
    @DisplayName("Should create default QuoteDto")
    void shouldCreateDefaultQuoteDto() {
        QuoteDto quote = new QuoteDto();

        assertThat(quote.getSymbol()).isNull();
        assertThat(quote.getPrice()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should be equal when same symbol and price")
    void shouldBeEqualWhenSameSymbolAndPrice() {
        QuoteDto quote1 = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
        QuoteDto quote2 = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
        assertThat(quote1)
            .isEqualTo(quote2)
            .hasSameHashCodeAs(quote2);
    }

    @Test
    @DisplayName("Should not be equal when different symbol")
    void shouldNotBeEqualWhenDifferentSymbol() {
        QuoteDto quote1 = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
        QuoteDto quote2 = new QuoteDto("GOOGL", VALID_PRICE);

        assertThat(quote1).isNotEqualTo(quote2);
    }

    @Test
    @DisplayName("Should not be equal when different price")
    void shouldNotBeEqualWhenDifferentPrice() {
        QuoteDto quote1 = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
        QuoteDto quote2 = new QuoteDto(VALID_SYMBOL, 160.0);

        assertThat(quote1).isNotEqualTo(quote2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToString() {
        QuoteDto quote = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
        String toString = quote.toString();
        assertThat(toString)
            .contains(VALID_SYMBOL)
            .contains(String.valueOf(VALID_PRICE));
    }
}
