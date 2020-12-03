package ni.org.jug.exchangerate.boundary;

import ni.org.jug.exchangerate.entity.Bank;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author aalaniz
 */
@Path("/api/banks")
@Produces(MediaType.APPLICATION_JSON)
public class BankResource {

    @GET
    public ResponseAsCollection findAll() {
        return new ResponseAsCollection(Bank.listAll());
    }

    @GET
    @Path("/{id}")
    public Bank findById(@PathParam("id") Integer id) {
        return Bank.findById(id);
    }

    @GET
    @Path("/description/{bank}")
    public Bank findByShortDescription(@PathParam("bank") String bank) {
        return Bank.findByShortDescription(bank);
    }
}
