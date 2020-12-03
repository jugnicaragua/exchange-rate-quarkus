package ni.org.jug.ws.rs;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * @author aalaniz
 */
@Provider
public class LocalDateConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(LocalDate.class)) {
            return (ParamConverter<T>) new LocalDateConverter();
        }
        return null;
    }
}
