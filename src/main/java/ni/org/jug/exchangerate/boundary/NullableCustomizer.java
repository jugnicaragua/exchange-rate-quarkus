package ni.org.jug.exchangerate.boundary;

import io.quarkus.jsonb.JsonbConfigCustomizer;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

/**
 * @author aalaniz
 */
@Singleton
public class NullableCustomizer implements JsonbConfigCustomizer {

    @Override
    public void customize(JsonbConfig jsonbConfig) {
        jsonbConfig.withNullValues(Boolean.TRUE);
    }
}
