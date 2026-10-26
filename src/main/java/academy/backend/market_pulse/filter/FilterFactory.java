package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Currency;

import java.math.BigDecimal;

/**
 * Выбор конкретного {@link InstrumentFilter} по критериям, переданным из CLI. Критерии
 * взаимоисключающие: одновременно задаётся не больше одного — по типу, по тикеру, по валюте
 * или по цене. Комбинирование нескольких критериев сразу — задача паттерна Chain of
 * Responsibility, за рамками этого семинара.
 */
public final class FilterFactory {

    private FilterFactory() {
    }

    public static InstrumentFilter create(String type, String ticker, Currency currency,
                                           PriceFilter.Operator priceOperator, BigDecimal price) {
        if (type != null) {
            return new TypeFilter(type);
        }
        if (ticker != null) {
            return new TickerFilter(ticker);
        }
        if (currency != null) {
            return new CurrencyFilter(currency);
        }
        if (priceOperator != null) {
            return new PriceFilter(priceOperator, price);
        }
        return new NoOpFilter();
    }
}
