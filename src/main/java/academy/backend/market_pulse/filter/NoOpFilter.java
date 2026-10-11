package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.model.Instrument;

public final class NoOpFilter implements InstrumentFilter {
    public static final NoOpFilter NO_FILTER = new NoOpFilter();

    private NoOpFilter() {};

    @Override
    public boolean matches(Instrument instrument) {
        return true;
    }
}
