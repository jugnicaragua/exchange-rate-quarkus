package ni.org.jug.exchangerate.boundary;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author aalaniz
 */
public class EntityNotFound extends WebApplicationException {
    private static final String ENTITY_WITH_FIELD_NOT_FOUND = "%s con %s = [%s] no fue encontrado";
    private static final String ENTITY_WITH_ID_NOT_FOUND = "%s con ID = [%s] no fue encontrado";

    private EntityNotFound(Response response) {
        super(response);
    }

    public static EntityNotFound create(String entityName, Object id) {
        Response response = Response.status(Response.Status.NOT_FOUND)
                .entity(String.format(ENTITY_WITH_ID_NOT_FOUND, entityName, id))
                .type(MediaType.TEXT_PLAIN)
                .build();
        return new EntityNotFound(response);
    }

    public static EntityNotFound create(String entityName, String field, Object value) {
        Response response = Response.status(Response.Status.BAD_REQUEST)
                .entity(String.format(ENTITY_WITH_FIELD_NOT_FOUND, entityName, field, value))
                .type(MediaType.TEXT_PLAIN)
                .build();
        return new EntityNotFound(response);
    }
}
