package ni.org.jug.exchangerate.boundary;

import ni.org.jug.exchangerate.entity.CommercialBankExchangeRate;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * @author aalaniz
 */
@Path("/api/commercialBankExchangeRates")
@Produces(MediaType.APPLICATION_JSON)
public class CommercialBankExchangeRateResource {

    @GET
    public ResponseAsCollection findAll() {
        return new ResponseAsCollection(CommercialBankExchangeRate.listAll());
    }

    @GET
    @Path("/{id}")
    public CommercialBankExchangeRate findById(@PathParam("id") Integer id) {
        return CommercialBankExchangeRate.findById(id);
    }

    @GET
    @Path("/date/{date:\\d{4}\\-\\d{2}\\-\\d{2}}")
    public ResponseAsCollection findByDate(@PathParam("date") LocalDate date) {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByDate(date));
    }

    @GET
    @Path("/date/today")
    public ResponseAsCollection today() {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByDate(LocalDate.now()));
    }

    @GET
    @Path("/date")
    public ResponseAsCollection findByDateBetween(@QueryParam("start") @DefaultValue("today") LocalDate start,
            @QueryParam("end") @DefaultValue("today") LocalDate end) {
        List<CommercialBankExchangeRate> exchangeRates = CommercialBankExchangeRate.findByDateBetween(start, end);
        return ExchangeRateDataByDate.asCollectionResponse(exchangeRates);
    }

    @GET
    @Path("/period/{year}-{month}")
    public ResponseAsCollection findByPeriod(@PathParam("year") int year, @PathParam("month") int month) {
        List<CommercialBankExchangeRate> exchangeRates = CommercialBankExchangeRate.findByPeriod(year, month);
        return ExchangeRateDataByDate.asCollectionResponse(exchangeRates);
    }

    @GET
    @Path("/period/current")
    public ResponseAsCollection findByCurrentPeriod() {
        List<CommercialBankExchangeRate> exchangeRates = CommercialBankExchangeRate.findByPeriod(YearMonth.now());
        return ExchangeRateDataByDate.asCollectionResponse(exchangeRates);
    }

    @GET
    @Path("/bank/{bank}")
    public ResponseAsCollection findByBank(@PathParam("bank") String bank) {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByBank(bank));
    }

    @GET
    @Path("/bank/{bank}/date/{date:\\d{4}\\-\\d{2}\\-\\d{2}}")
    public ResponseAsCollection findByBankAndDate(@PathParam("bank") String bank, @PathParam("date") LocalDate date) {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByBankAndDate(bank, date));
    }

    @GET
    @Path("/bank/{bank}/date")
    public ResponseAsCollection findByBankAndDateBetween(@PathParam("bank") String bank, @QueryParam("start") LocalDate start,
            @QueryParam("end") LocalDate end) {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByBankAndDateBetween(bank, start, end));
    }

    @GET
    @Path("/bank/{bank}/{year:\\d+}-{month:\\d+}")
    public ResponseAsCollection findByBankAndPeriod(@PathParam("bank") String bank, @PathParam("year") int year,
            @PathParam("month") int month) {
        return new ResponseAsCollection(CommercialBankExchangeRate.findByBankAndPeriod(bank, year, month));
    }
}
