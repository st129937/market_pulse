package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.cli.AddCommand;
import academy.backend.market_pulse.cli.ListCommand;
import academy.backend.market_pulse.cli.MarketPulseCli;
import academy.backend.market_pulse.cli.SearchCommand;
import academy.backend.market_pulse.model.Bond;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Etf;
import academy.backend.market_pulse.model.Stock;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine;

import java.math.BigDecimal;

/**
 * Точка входа CLI. Команды регистрируются вручную через {@code addSubcommand}, а не через
 * атрибут {@code subcommands} аннотации {@code @Command}: у команд нет конструктора без
 * аргументов, picocli не смог бы создать их самостоятельно по одному классу — каждой нужен уже
 * готовый {@code repository}.
 * <p>
 * mvn exec:java "-Dexec.mainClass=academy.backend.market_pulse.demo.Main" "-Dexec.args=list --type BOND"
 */
public class Main {

    public static void main(String[] args) {
        InstrumentRepository repository = new InstrumentRepository();

        repository.add(new Bond("SU26238", "ОФЗ 26238", Currency.RUB, new BigDecimal("7.1"), 2041));
        repository.add(new Stock("SBER", "Сбербанк", Currency.RUB, "Финансы", new BigDecimal("6.5")));
        repository.add(new Etf("FXUS", "FinEx US", Currency.USD, "S&P 500"));


        CommandLine cli = new CommandLine(new MarketPulseCli())
                .addSubcommand(new SearchCommand(repository))
                .addSubcommand(new AddCommand(repository))
                .addSubcommand(new ListCommand(repository));
        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }
}
