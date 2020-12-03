package ni.org.jug.exchangerate.boundary;

import java.util.Collection;
import java.util.Objects;

/**
 * @author aalaniz
 */
public final class ResponseAsCollection {

    public final int size;
    public final Collection data;

    public ResponseAsCollection(Collection data) {
        Objects.requireNonNull(data);
        this.size = data.size();
        this.data = data;
    }
}
