package ni.org.jug.exchangerate.boundary;

import ni.org.jug.exchangerate.entity.CentralBankExchangeRate;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * @author aalaniz
 */
@Path("/api/centralBankExchangeRates")
@Produces(MediaType.APPLICATION_JSON)
public class CentralBankExchangeRateResource {

    @GET
    public ResponseAsCollection findAll() {
        return new ResponseAsCollection(CentralBankExchangeRate.listAll());
    }

    @GET
    @Path("/{year:\\d+}-{month:\\d+}-{day:\\d+}")
    public CentralBankExchangeRate findById(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {
        return CentralBankExchangeRate.findById(year, month, day);
    }

    @GET
    @Path("/today")
    public CentralBankExchangeRate today() {
        return CentralBankExchangeRate.findById(LocalDate.now());
    }

    @GET
    @Path("/id")
    public ResponseAsCollection findByIdBetween(@QueryParam("start") @DefaultValue("today") LocalDate start,
            @QueryParam("end") @DefaultValue("today") LocalDate end) {
        return new ResponseAsCollection(CentralBankExchangeRate.findByIdBetween(start, end));
    }

    @GET
    @Path("/{year:\\d+}-{month:\\d+}")
    public ResponseAsCollection findByPeriod(@PathParam("year") int year, @PathParam("month") int month) {
        return new ResponseAsCollection(CentralBankExchangeRate.findByPeriod(year, month));
    }

    @GET
    @Path("/period")
    public ResponseAsCollection findByPeriodBetween(@QueryParam("start") @DefaultValue("today") LocalDate start,
            @QueryParam("end") @DefaultValue("today") LocalDate end) {
        return new ResponseAsCollection(CentralBankExchangeRate.findByPeriodBetween(start, end));
    }

    @GET
    @Path("/period/current")
    public ResponseAsCollection findByCurrentPeriod() {
        return new ResponseAsCollection(CentralBankExchangeRate.findByPeriod(YearMonth.now()));
    }

    @GET
    @Path("/period/next")
    public ResponseAsCollection findByNextPeriod() {
        return new ResponseAsCollection(CentralBankExchangeRate.findByPeriod(YearMonth.now().plusMonths(1)));
    }
}