package ni.org.jug.exchangerate.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author aalaniz
 */
@MappedSuperclass
public abstract class SerialIdentifier<T extends Number> extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "seq", strategy = GenerationType.SEQUENCE)
    protected T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SerialIdentifier<?> other = (SerialIdentifier<?>) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
