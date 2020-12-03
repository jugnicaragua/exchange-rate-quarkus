package ni.org.jug.exchangerate.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import ni.org.jug.exchangerate.boundary.EntityNotFound;
import ni.org.jug.exchangerate.boundary.InvalidInputException;
import ni.org.jug.exchangerate.boundary.InvalidRangeException;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aalaniz
 */
@Entity
@Table(name = "ncb_exchange_rate")
@JsonbTypeAdapter(CentralBankExchangeRate.CentralBankExchangeRateAdapter.class)
public class CentralBankExchangeRate extends Identifier<Integer> {
    public static final String ENTITY_NAME = "Tipo de Cambio Oficial";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @NotNull
    @Column(name = "exchange_rate_date")
    private LocalDate date;

    @NotNull
    @Column(name = "exchange_rate_amount")
    private BigDecimal amount;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    @PrePersist
    public void onBeforeSave() {
        if (date == null) {
            throw new InvalidInputException("La [fecha] es un dato requerido para calcular el ID");
        }
        id = generateId(date);
    }

    public static class CentralBankExchangeRateAdapter implements JsonbAdapter<CentralBankExchangeRate, Map<String, Object>> {

        @Override
        public Map<String, Object> adaptToJson(CentralBankExchangeRate entity) throws Exception {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", entity.id);
            response.put("currency", entity.currency.getShortDescriptionAndSymbol());
            response.put("date", entity.date);
            response.put("amount", entity.amount);
            response.put("createdOn", entity.auditTrail.getCreatedOn());
            response.put("updatedOn", entity.auditTrail.getUpdatedOn());
            return response;
        }

        @Override
        public CentralBankExchangeRate adaptFromJson(Map<String, Object> map) throws Exception {
            throw new UnsupportedOperationException("La deserializacion a la entidad CentralBankExchangeRate no esta soportada");
        }
    }

    public static Integer generateId(int year, int month, int day) {
        int id = (year*100 + month)*100 + day;
        return id;
    }

    public static Integer generateId(LocalDate date) {
        return generateId(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static CentralBankExchangeRate createOf(Currency currency, LocalDate date, BigDecimal amount) {
        CentralBankExchangeRate centralBankExchangeRate = new CentralBankExchangeRate();
        centralBankExchangeRate.setCurrency(currency);
        centralBankExchangeRate.setDate(date);
        centralBankExchangeRate.setAmount(amount);
        return centralBankExchangeRate;
    }

    public static List<CentralBankExchangeRate> listAll() {
        return list("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr");
    }

    public static CentralBankExchangeRate findById(Integer id) throws EntityNotFound {
        PanacheQuery<CentralBankExchangeRate> query = find("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr " +
                "WHERE c.id = ?1", id);
        return query.firstResultOptional().orElseThrow(() -> EntityNotFound.create(ENTITY_NAME, id));
    }

    public static CentralBankExchangeRate findById(int year, int month, int day) throws EntityNotFound {
        return findById(generateId(year, month, day));
    }

    public static CentralBankExchangeRate findById(LocalDate date) throws EntityNotFound {
        return findById(generateId(date));
    }

    public static List<CentralBankExchangeRate> findByIdBetween(LocalDate startDate, LocalDate endDate) {
        Integer start = CentralBankExchangeRate.generateId(startDate);
        Integer end = CentralBankExchangeRate.generateId(endDate);

        if (start.compareTo(end) > 0) {
            throw InvalidRangeException.create(start, end);
        }

        return list("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr " +
                "WHERE c.id BETWEEN ?1 AND ?2", start, end);
    }

    public static List<CentralBankExchangeRate> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw InvalidRangeException.create(startDate, endDate);
        }

        return list("SELECT c FROM CentralBankExchangeRate c JOIN FETCH c.currency curr " +
                "WHERE c.date BETWEEN ?1 AND ?2", startDate, endDate);
    }

    public static List<CentralBankExchangeRate> findByPeriod(int year, int month) {
        YearMonth period = YearMonth.of(year, month);
        return findByDateBetween(period.atDay(1), period.atEndOfMonth());
    }

    public static List<CentralBankExchangeRate> findByPeriod(YearMonth period) {
        return findByDateBetween(period.atDay(1), period.atEndOfMonth());
    }

    public static List<CentralBankExchangeRate> findByPeriodBetween(LocalDate startDate, LocalDate endDate) {
        YearMonth period1 = YearMonth.from(startDate);
        YearMonth period2 = YearMonth.from(endDate);

        if (period1.isAfter(period2)) {
            throw InvalidRangeException.create(period1, period2);
        }

        return findByDateBetween(period1.atDay(1), period2.atEndOfMonth());
    }

    public static long countByDateBetween(LocalDate startDate, LocalDate endDate) {
        return count("date BETWEEN ?1 AND ?2", startDate, endDate);
    }

    @Override
    public String toString() {
        return "CentralBankExchangeRate{" +
                "currency=" + currency +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }
}
