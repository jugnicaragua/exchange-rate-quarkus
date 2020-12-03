package ni.org.jug.ws.rs;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;

/**
 * @author aalaniz
 */
public class LocalDateConverter implements ParamConverter<LocalDate> {
    private static final String TODAY = "today";
    private static final String NOW = "now";

    @Override
    public LocalDate fromString(String value) {
        if (value == null) {
            return null;
        }
        if (TODAY.equalsIgnoreCase(value) || NOW.equalsIgnoreCase(value)) {
            return LocalDate.now();
        }
        return LocalDate.parse(value);
    }

    @Override
    public String toString(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
