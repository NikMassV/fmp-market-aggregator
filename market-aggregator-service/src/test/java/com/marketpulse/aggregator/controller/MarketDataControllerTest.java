package com.marketpulse.aggregator.controller;

import com.marketpulse.aggregator.dto.QuoteDto;
import com.marketpulse.aggregator.exception.FmpApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Market Data Controller Tests")
class MarketDataControllerTest {

    private static final String TEST_API_KEY = "test-key";
    private static final String TEST_BASE_URL = "http://test-url";
    private static final String VALID_SYMBOL = "AAPL";
    private static final String INVALID_SYMBOL = "INVALID";
    private static final double VALID_PRICE = 150.0;

    @Mock
    private WebClient mockWebClient;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;
    @Mock
    private WebClient.RequestHeadersSpec headersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private MarketDataController controller;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(mockWebClient);
        when(mockWebClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        controller = new MarketDataController(webClientBuilder, TEST_API_KEY, TEST_BASE_URL);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Nested
    @DisplayName("Get Quote Tests")
    class GetQuoteTests {

        @Test
        @DisplayName("Should return quote when valid symbol is provided")
        void getQuote_returnsQuote() {
            QuoteDto mockQuote = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(Flux.just(mockQuote));

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(QuoteDto.class)
                    .isEqualTo(mockQuote);
        }
    }

    @Nested
    @DisplayName("FMP API Error Tests")
    class FmpApiErrorTests {

        @Test
        @DisplayName("Should return bad gateway when FMP API returns error")
        void getQuote_fmpApiError_returnsBadGateway() {
            Flux<QuoteDto> errorFlux = Flux.error(new FmpApiException("FMP API error: 500 Internal Server Error"));
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(errorFlux);

            WebTestClient testClient = WebTestClient.bindToController(controller)
                    .controllerAdvice(new com.marketpulse.aggregator.exception.GlobalExceptionHandler())
                    .build();

            testClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains("FMP API error"));
        }

        @Test
        @DisplayName("Should return bad gateway when FMP API returns 404")
        void getQuote_fmpApiNotFound_returnsBadGateway() {
            Flux<QuoteDto> errorFlux = Flux.error(new FmpApiException("FMP API error: 404 Not Found"));
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(errorFlux);

            WebTestClient testClient = WebTestClient.bindToController(controller)
                    .controllerAdvice(new com.marketpulse.aggregator.exception.GlobalExceptionHandler())
                    .build();

            testClient.get()
                    .uri("/quotes/{symbol}", INVALID_SYMBOL)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains("FMP API error"));
        }

        @Test
        @DisplayName("Should return bad gateway when FMP API returns 401")
        void getQuote_fmpApiUnauthorized_returnsBadGateway() {
            Flux<QuoteDto> errorFlux = Flux.error(new FmpApiException("FMP API error: 401 Unauthorized"));
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(errorFlux);

            WebTestClient testClient = WebTestClient.bindToController(controller)
                    .controllerAdvice(new com.marketpulse.aggregator.exception.GlobalExceptionHandler())
                    .build();

            testClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains("FMP API error"));
        }
    }

    @Nested
    @DisplayName("Timeout Tests")
    class TimeoutTests {

        @Test
        @DisplayName("Should return server error when request times out")
        void getQuote_timeout_returnsServerError() {
            Flux<QuoteDto> slowFlux = Flux.just(new QuoteDto(VALID_SYMBOL, VALID_PRICE))
                    .delayElements(Duration.ofSeconds(5));
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(slowFlux);

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().is5xxServerError();
        }

        @Test
        @DisplayName("Should return server error when request takes too long")
        void getQuote_slowResponse_returnsServerError() {
            Flux<QuoteDto> slowFlux = Flux.just(new QuoteDto(VALID_SYMBOL, VALID_PRICE))
                    .delayElements(Duration.ofSeconds(10));
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(slowFlux);

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().is5xxServerError();
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle multiple quotes from FMP API")
        void getQuote_multipleQuotes_returnsFirstQuote() {
            QuoteDto firstQuote = new QuoteDto(VALID_SYMBOL, VALID_PRICE);
            QuoteDto secondQuote = new QuoteDto(VALID_SYMBOL, 160.0);
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(Flux.just(firstQuote, secondQuote));

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(QuoteDto.class)
                    .isEqualTo(firstQuote);
        }

        @Test
        @DisplayName("Should handle zero price from FMP API")
        void getQuote_zeroPrice_returnsQuote() {
            QuoteDto mockQuote = new QuoteDto(VALID_SYMBOL, 0.0);
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(Flux.just(mockQuote));

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(QuoteDto.class)
                    .isEqualTo(mockQuote);
        }

        @Test
        @DisplayName("Should handle negative price from FMP API")
        void getQuote_negativePrice_returnsQuote() {
            QuoteDto mockQuote = new QuoteDto(VALID_SYMBOL, -10.0);
            when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(Flux.just(mockQuote));

            webTestClient.get()
                    .uri("/quotes/{symbol}", VALID_SYMBOL)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(QuoteDto.class)
                    .isEqualTo(mockQuote);
        }
    }
}
