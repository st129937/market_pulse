package academy.backend.market_pulse.source;

import academy.backend.market_pulse.model.Quote;

import java.util.Map;
import java.util.Optional;

public class QuoteSource {

    private final Map<String, Quote> data;

    public QuoteSource(Map<String, Quote> data) {
        this.data = data;
    }

    public Optional<Quote> fetch(String ticker) {
        return Optional.ofNullable(data.get(ticker.toUpperCase()));
    }
}