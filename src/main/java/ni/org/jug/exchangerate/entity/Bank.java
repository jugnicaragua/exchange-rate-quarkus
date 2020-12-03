package ni.org.jug.exchangerate.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import ni.org.jug.exchangerate.boundary.EntityNotFound;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author aalaniz
 */
@Entity
@Table(name = "bank")
@SequenceGenerator(name = "seq", sequenceName = "bank_id_seq", allocationSize = 1)
@JsonbTypeAdapter(Bank.BankAdapter.class)
public class Bank extends IntegerSerialIdentifier {
    public static final String ENTITY_NAME = "Banco";
    public static final String SEARCHABLE_DESCRIPTION = "descripcion";

    @Valid
    @Embedded
    private Description description = new Description();

    @NotEmpty
    @Size(min = 1, max = 200)
    @Column(name = "url")
    private String url;

    @NotNull
    @Column(name = "is_active")
    private Boolean active;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "bank_id", nullable = false)
    private Set<Cookie> cookies = new HashSet<>();

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public void removeCookie(Cookie cookie) {
        this.cookies.remove(cookie);
    }

    public Cookie findCookieById(Integer id) {
        Objects.requireNonNull(id);
        return cookies.stream()
                .filter(cookie -> id.equals(cookie.getId()))
                .findFirst()
                .orElseThrow(() -> EntityNotFound.create(Cookie.ENTITY_NAME, id));
    }

    public static Bank createOf(String name, String description, String url) {
        Bank bank = new Bank();
        bank.getDescription().setShortDescription(name);
        bank.getDescription().setDescription(description);
        bank.setUrl(url);
        bank.setActive(Boolean.TRUE);
        return bank;
    }

    public static class BankAdapter implements JsonbAdapter<Bank, Map<String, Object>> {

        @Override
        public Map<String, Object> adaptToJson(Bank bank) throws Exception {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", bank.id);
            response.put("shortDescription", bank.description.getShortDescription());
            response.put("description", bank.description.getDescription());
            response.put("url", bank.url);
            response.put("active", bank.active);
            response.put("createdOn", bank.auditTrail.getCreatedOn());
            response.put("updatedOn", bank.auditTrail.getUpdatedOn());
            response.put("cookies", bank.cookies);
            return response;
        }

        @Override
        public Bank adaptFromJson(Map<String, Object> map) throws Exception {
            throw new UnsupportedOperationException("La deserializacion a la entidad Bank no esta soportada");
        }
    }

    public static List<Bank> listAll() {
        return list("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c");
    }

    public static Bank findById(Integer id) {
        PanacheQuery<Bank> query = find("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c " +
                "WHERE b.id = ?1", id);
        return query.firstResultOptional().orElseThrow(() -> EntityNotFound.create(ENTITY_NAME, id));
    }

    public static Bank findByShortDescription(String bank) {
        PanacheQuery<Bank> query = find("SELECT DISTINCT b FROM Bank b LEFT JOIN FETCH b.cookies c " +
                "WHERE b.description.shortDescription = ?1", bank);
        return query.firstResultOptional().orElseThrow(() -> EntityNotFound.create(ENTITY_NAME, SEARCHABLE_DESCRIPTION, bank));
    }

    @Override
    public String toString() {
        return "Bank{" +
                "description=" + description +
                ", active=" + active +
                '}';
    }
}
