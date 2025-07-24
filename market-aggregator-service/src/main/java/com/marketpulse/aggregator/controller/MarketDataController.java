package com.marketpulse.aggregator.controller;

import com.marketpulse.aggregator.dto.QuoteDto;
import com.marketpulse.aggregator.exception.FmpApiException;
import com.marketpulse.aggregator.producer.MarketQuoteProducer;
import com.marketpulse.avro.MarketQuote;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

@RestController
@Validated
@Tag(name = "Market Data", description = "Endpoints for fetching market data from FMP API")
public class MarketDataController {

    private static final Logger log = LoggerFactory.getLogger(MarketDataController.class);
    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;
    private final MarketQuoteProducer marketQuoteProducer;

    @Autowired
    public MarketDataController(WebClient.Builder webClientBuilder,
                                @Value("${fmp.api.key:demo}") String apiKey,
                                @Value("${fmp.api.base-url}") String baseUrl,
                                MarketQuoteProducer marketQuoteProducer) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder.baseUrl(this.baseUrl).build();
        this.marketQuoteProducer = marketQuoteProducer;
    }

    @Operation(summary = "Get stock quote by symbol", description = "Fetches the latest stock quote for the given symbol from FMP API.")
    @GetMapping(value = "/quotes/{symbol}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<QuoteDto> getQuote(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Symbol must not be blank");
        }
        log.info("Received request for quote");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote/{symbol}")
                        .queryParam("apikey", apiKey)
                        .build(symbol))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("FMP API error for symbol {}: {}", symbol, body);
                            return Mono.error(new FmpApiException("FMP API error: " + body));
                        }))
                .bodyToFlux(QuoteDto.class)
                .next()
                .timeout(Duration.ofSeconds(3))
                .doOnNext(quote -> {
                    log.info("Fetched quote for {}: {}", symbol, quote);
                    MarketQuote avroQuote = MarketQuote.newBuilder()
                        .setSymbol(quote.getSymbol())
                        .setPrice(quote.getPrice())
                        .setName(null)
                        .setChangesPercentage(null)
                        .setChange(null)
                        .setDayLow(null)
                        .setDayHigh(null)
                        .setYearHigh(null)
                        .setYearLow(null)
                        .setMarketCap(null)
                        .setPriceAvg50(null)
                        .setPriceAvg200(null)
                        .setVolume(null)
                        .setAvgVolume(null)
                        .setExchange(null)
                        .setOpen(null)
                        .setPreviousClose(null)
                        .setEps(null)
                        .setPe(null)
                        .setEarningsAnnouncement(null)
                        .setSharesOutstanding(null)
                        .setTimestamp(null)
                        .build();
                    marketQuoteProducer.send(avroQuote);
                })
                .doOnError(e -> log.error("Error fetching quote for {}: {}", symbol, e.getMessage()));
    }
}
