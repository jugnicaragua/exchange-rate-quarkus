package ni.org.jug.exchangerate.entity;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aalaniz
 */
@Entity
@Table(name = "cookie")
@SequenceGenerator(name = "seq", sequenceName = "cookie_id_seq", allocationSize = 1)
@JsonbTypeAdapter(Cookie.CookieAdapter.class)
public class Cookie extends IntegerSerialIdentifier {
    public static final String ENTITY_NAME = "Cookie";

    @NotEmpty
    @Size(min = 3, max = 50)
    @Column(name = "name")
    private String name;

    @NotEmpty
    @Size(min = 3, max = 150)
    @Column(name = "value")
    private String value;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public static class CookieAdapter implements JsonbAdapter<Cookie, Map<String, Object>> {

        @Override
        public Map<String, Object> adaptToJson(Cookie cookie) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", cookie.id);
            response.put("name", cookie.name);
            response.put("value", cookie.value);
            response.put("createdOn", cookie.auditTrail.getCreatedOn());
            response.put("updatedOn", cookie.auditTrail.getUpdatedOn());
            return response;
        }

        @Override
        public Cookie adaptFromJson(Map<String, Object> map) {
            Cookie cookie = new Cookie();
            Number id = (Number) map.get("id");
            cookie.id = id == null ? null : id.intValue();
            cookie.name = (String) map.get("name");
            cookie.value = (String) map.get("value");
            cookie.getAuditTrail().setCreatedOn((LocalDateTime) map.get("createdOn"));
            cookie.getAuditTrail().setUpdatedOn((LocalDateTime) map.get("updatedOn"));
            return cookie;
        }
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
