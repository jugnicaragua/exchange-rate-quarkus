package ni.org.jug.exchangerate.boundary;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * @author aalaniz
 */
public class InvalidRangeException extends WebApplicationException {
    private static final String INVALID_RANGE_MESSAGE = "El 1er parametro de tipo %s con valor [%s] es mayor que el 2do parametro de " +
            "tipo %s con valor [%s]";

    private InvalidRangeException(Response response) {
        super(response);
    }

    public static InvalidRangeException create(Object value1, Object value2) {
        Objects.requireNonNull(value1);
        Objects.requireNonNull(value2);
        String message = String.format(INVALID_RANGE_MESSAGE, value1.getClass().getSimpleName(), value1, value2.getClass().getSimpleName(),
                value2);
        Response response = Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
        return new InvalidRangeException(response);
    }
}
