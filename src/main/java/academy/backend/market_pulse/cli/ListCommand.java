package academy.backend.market_pulse.cli;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import academy.backend.market_pulse.filter.FilterFactory;
import academy.backend.market_pulse.filter.InstrumentFilter;
import academy.backend.market_pulse.filter.PriceFilter;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "list", description = "Список инструментов")
public class ListCommand implements Callable<Integer> {

    @Option(names = "--type", description = "Фильтр по типу инструмента")
    private String type;

    @Option(names = "--ticker", description = "Фильтр по подстроке в тикере")
    private String ticker;

    @Option(names = "--currency", description = "Фильтр по валюте инструмента")
    private Currency currency;

    @Option(names = "--price-op", description = "Оператор сравнения цены: GE, LE или EQ")
    private PriceFilter.Operator priceOperator;

    @Option(names = "--price", description = "Пороговое значение цены (дивидендная доходность акции)")
    private BigDecimal price;

    private final InstrumentRepository repository;

    public ListCommand(InstrumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Integer call() {
        checkCriteria();

        if (type != null
                && !type.equalsIgnoreCase("STOCK")
                && !type.equalsIgnoreCase("BOND")
                && !type.equalsIgnoreCase("ETF")) {
            throw new CliException("Неизвестный тип инструмента: " + type
                    + ". Допустимо: STOCK, BOND, ETF");
        }

        InstrumentFilter filter = FilterFactory.create(type, ticker, currency, priceOperator, price);

        try {
            filter = FilterFactory.create(type, ticker, currency, priceOperator, price);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CliException("Не получилось создать фильтр: " + e.getMessage(), e);
        }

        for (Instrument instrument : repository) {
            boolean matched;
            try {
                matched = filter.matches(instrument);
            } catch (RuntimeException e) {
                throw new CliException(
                        "Ошибка при фильтрации инструмента '" + instrument.getTicker() + "': " + e.getMessage(), e);
            }
            if (matched) {
                System.out.println(instrument.getDescription());
            }
        }
        return 0;
    }

    private static int count(boolean... flags) {
        int matched = 0;
        for (boolean flag : flags) {
            if (flag) {
                matched++;
            }
        }
        return matched;
    }

    private void checkCriteria() {
        int criteriaCount = count(type != null, ticker != null, currency != null, priceOperator != null);
        if (criteriaCount > 1) {
            throw new CliException(
                    "Одновременно можно задать только один критерий отбора: --type, --ticker, --currency или "
                            + "--price-op/--price");
        }
        if ((priceOperator != null && price == null) || (priceOperator == null && price != null)) {
            throw new CliException("Параметры --price-op и --price должны передаваться вместе");
        }
    }
}
