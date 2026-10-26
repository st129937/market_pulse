package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.cli.*;
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

        // обработка ошибки парсинга аргументов
        cli.setParameterExceptionHandler((ex, cmdArgs) -> {
            System.err.println("Ошибка в аргументах: " + ex.getMessage());
            ex.getCommandLine().usage(System.err);
            return CommandLine.ExitCode.USAGE;
        });

        // обработка ошибок выполнения команд
        cli.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            if (ex instanceof CliException) {
                System.err.println("Ошибка: " + ex.getMessage());
            } else if (ex instanceof IllegalArgumentException) {
                System.err.println("Ошибка: " + ex.getMessage());
            } else {
                System.err.println("Непредвиденная ошибка: " + ex.getMessage());
            }
            return CommandLine.ExitCode.SOFTWARE;
        });

        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }
}
