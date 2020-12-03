package ni.org.jug.exchangerate.boundary;

import ni.org.jug.exchangerate.entity.CommercialBankExchangeRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author aalaniz
 */
public final class ExchangeRateDataByDate {

    public final LocalDate date;
    public final List<CommercialBankExchangeRate> exchangeRates;

    private ExchangeRateDataByDate(LocalDate date, List<CommercialBankExchangeRate> exchangeRates) {
        this.date = date;
        this.exchangeRates = exchangeRates;
    }

    public static List<ExchangeRateDataByDate> groupByDay(List<CommercialBankExchangeRate> exchangeRates) {
        Map<LocalDate, List<CommercialBankExchangeRate>> exchangeRateByDates = Objects.requireNonNull(exchangeRates)
                .stream()
                .collect(Collectors.groupingBy(CommercialBankExchangeRate::getDate));
        List<ExchangeRateDataByDate> responseByDates = exchangeRateByDates.entrySet()
                .stream()
                .map(entry -> new ExchangeRateDataByDate(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return responseByDates;
    }

    public static ResponseAsCollection asCollectionResponse(List<CommercialBankExchangeRate> exchangeRates) {
        return new ResponseAsCollection(groupByDay(exchangeRates));
    }
}
