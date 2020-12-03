package ni.org.jug.exchangerate.boundary;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author aalaniz
 */
public class InvalidInputException extends WebApplicationException {

    public InvalidInputException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity(message)
                .type(MediaType.TEXT_PLAIN)
                .build());
    }

    public static InvalidInputException requireNullId(String entityName) {
        String msg = String.format("El [id] de la entidad <%s> debe ser null", entityName);
        return new InvalidInputException(msg);
    }

    public static InvalidInputException requireNonNullId(String entityName) {
        String msg = String.format("El [id] de la entidad <%s> es requerido", entityName);
        return new InvalidInputException(msg);
    }
}
