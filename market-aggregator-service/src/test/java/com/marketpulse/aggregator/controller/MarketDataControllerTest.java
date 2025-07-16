package com.marketpulse.aggregator.controller;

import com.marketpulse.aggregator.dto.QuoteDto;
import com.marketpulse.aggregator.exception.FmpApiException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketDataControllerTest {

    private final String testApiKey = "test-key";
    private final String testBaseUrl = "http://test-url";

    private MarketDataController createController(WebClient mockWebClient) {
        WebClient.Builder webClientBuilder = Mockito.mock(WebClient.Builder.class);
        Mockito.when(webClientBuilder.baseUrl(Mockito.anyString())).thenReturn(webClientBuilder);
        Mockito.when(webClientBuilder.build()).thenReturn(mockWebClient);
        return new MarketDataController(webClientBuilder, testApiKey, testBaseUrl);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientChain(WebClient mockWebClient, Flux<QuoteDto> resultFlux) {
        WebClient.RequestHeadersUriSpec uriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(mockWebClient.get()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri(Mockito.any(Function.class))).thenReturn(headersSpec);
        Mockito.when(headersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.onStatus(Mockito.any(), Mockito.any())).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToFlux(QuoteDto.class)).thenReturn(resultFlux);
    }

    @Test
    void getQuote_returnsQuote() {
        String symbol = "AAPL";
        QuoteDto mockQuote = new QuoteDto(symbol, 150.0);
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        mockWebClientChain(mockWebClient, Flux.just(mockQuote));

        MarketDataController controller = createController(mockWebClient);
        WebTestClient testClient = WebTestClient.bindToController(controller).build();

        testClient.get()
                .uri("/quotes/{symbol}", symbol)
                .exchange()
                .expectStatus().isOk()
                .expectBody(QuoteDto.class)
                .isEqualTo(mockQuote);
    }

    @Test
    void getQuote_invalidSymbol_returnsBadRequest() {
        MarketDataController controller = createController(Mockito.mock(WebClient.class));
        WebTestClient testClient = WebTestClient.bindToController(controller).build();

        testClient.get()
                .uri("/quotes/{symbol}", " ") // use blank string to trigger @NotBlank
                .exchange()
                .expectStatus().is4xxClientError(); // Accept any 4xx error
    }

    @Test
    void getQuote_fmpApiError_returnsInternalServerError() {
        String symbol = "AAPL";
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        Flux<QuoteDto> errorFlux = Flux.error(new FmpApiException("FMP API error: 500 Internal Server Error"));
        mockWebClientChain(mockWebClient, errorFlux);

        MarketDataController controller = createController(mockWebClient);
        WebTestClient testClient = WebTestClient.bindToController(controller).controllerAdvice(new com.marketpulse.aggregator.exception.GlobalExceptionHandler()).build();

        testClient.get()
                .uri("/quotes/{symbol}", symbol)
                .exchange()
                .expectStatus().isEqualTo(org.springframework.http.HttpStatus.BAD_GATEWAY)
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("FMP API error"));
    }

    @Test
    void getQuote_timeout_returnsServerError() {
        String symbol = "AAPL";
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        Flux<QuoteDto> slowFlux = Flux.just(new QuoteDto(symbol, 150.0))
                                      .delayElements(Duration.ofSeconds(5));
        mockWebClientChain(mockWebClient, slowFlux);

        MarketDataController controller = createController(mockWebClient);
        WebTestClient testClient = WebTestClient.bindToController(controller).build();

        testClient.get()
                .uri("/quotes/{symbol}", symbol)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
