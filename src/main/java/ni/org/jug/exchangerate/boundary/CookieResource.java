package ni.org.jug.exchangerate.boundary;

import io.quarkus.security.Authenticated;
import ni.org.jug.exchangerate.control.BankService;
import ni.org.jug.exchangerate.entity.Bank;
import ni.org.jug.exchangerate.entity.Cookie;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Set;

/**
 * @author aalaniz
 */
@Path("/api/banks/{id}/cookies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CookieResource {

    @Inject
    BankService bankService;

    @GET
    public ResponseAsCollection findAll(@PathParam("id") Integer bankId) {
        Set<Cookie> cookies = Bank.findById(bankId).getCookies();
        return new ResponseAsCollection(cookies);
    }

    @GET
    @Path("/{cookieId}")
    public Cookie findById(@PathParam("id") Integer bankId, @PathParam("cookieId") Integer cookieId) {
        return Bank.findById(bankId).findCookieById(cookieId);
    }

    @POST
    @Authenticated
    public Response create(@PathParam("id") Integer bankId, Cookie cookie, @Context UriInfo uriInfo) {
        bankService.addCookie(bankId, cookie);

        URI uri = uriInfo.getAbsolutePathBuilder().path("{cookieId}").build(cookie.getId());
        return Response.created(uri).build();
    }

    @PUT
    @Authenticated
    public void update(@PathParam("id") Integer bankId, CookieRequest cookieRequest) {
        bankService.updateCookie(bankId, cookieRequest);
    }

    @DELETE
    @Path("/{cookieId}")
    @Authenticated
    public void delete(@PathParam("id") Integer bankId, @PathParam("cookieId") Integer cookieId) {
        bankService.removeCookie(bankId, cookieId);
    }
}
