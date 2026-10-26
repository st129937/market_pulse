package academy.backend.market_pulse.cli;

import academy.backend.market_pulse.service.QuoteService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "quote", description = "Запрос котировок по тикерам (FIFO + кеш)")
public class QuoteCommand implements Callable<Integer> {

    // arity = "1..*" тк picocli потребует хотя бы один тикер
    @Parameters(arity = "1..*", paramLabel = "TICKER", description = "Один или несколько тикеров")
    private List<String> tickers;

    private final QuoteService quoteService;

    public QuoteCommand(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Override
    public Integer call() {
        // складываем тикеры в очередь
        for (String ticker : tickers) {
            quoteService.request(ticker);
        }
        // идем по очереди с головы
        quoteService.processAll();

        for (String ticker : tickers) {
            quoteService.cached(ticker).ifPresent(System.out::println);
        }
        return 0;
    }
}