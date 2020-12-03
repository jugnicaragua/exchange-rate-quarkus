package ni.org.jug.exchangerate.entity;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import ni.org.jug.exchangerate.boundary.EntityNotFound;
import ni.org.jug.exchangerate.boundary.InvalidRangeException;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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
@Table(name = "cb_exchange_rate")
@SequenceGenerator(name = "seq", sequenceName = "cb_exchange_rate_id_seq", allocationSize = 1)
@JsonbTypeAdapter(CommercialBankExchangeRate.CommercialBankExchangeRateAdapter.class)
public class CommercialBankExchangeRate extends IntegerSerialIdentifier {
    public static final String ENTITY_NAME = "Compra/Venta de dolares";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @NotNull
    @PastOrPresent
    @Column(name = "exchange_rate_date")
    private LocalDate date;

    @NotNull
    @Column(name = "sell")
    private BigDecimal sell;

    @NotNull
    @Column(name = "buy")
    private BigDecimal buy;

    @NotNull
    @Column(name = "is_best_sell_price")
    private Boolean bestSellPrice;

    @NotNull
    @Column(name = "is_best_buy_price")
    private Boolean bestBuyPrice;

    @Embedded
    private AuditTrail auditTrail = new AuditTrail();

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public Boolean getBestSellPrice() {
        return bestSellPrice;
    }

    public void setBestSellPrice(Boolean bestSellPrice) {
        this.bestSellPrice = bestSellPrice;
    }

    public Boolean getBestBuyPrice() {
        return bestBuyPrice;
    }

    public void setBestBuyPrice(Boolean bestBuyPrice) {
        this.bestBuyPrice = bestBuyPrice;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public static class CommercialBankExchangeRateAdapter implements JsonbAdapter<CommercialBankExchangeRate, Map<String, Object>> {

        @Override
        public Map<String, Object> adaptToJson(CommercialBankExchangeRate entity) throws Exception {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("id", entity.id);
            response.put("currency", entity.currency.getShortDescriptionAndSymbol());
            response.put("bank", entity.bank.getDescription().getShortDescription());
            response.put("date", entity.date);
            response.put("sell", entity.sell);
            response.put("buy", entity.buy);
            response.put("bestSellPrice", entity.bestSellPrice);
            response.put("bestBuyPrice", entity.bestBuyPrice);
            response.put("createdOn", entity.auditTrail.getCreatedOn());
            response.put("updatedOn", entity.auditTrail.getUpdatedOn());
            return response;
        }

        @Override
        public CommercialBankExchangeRate adaptFromJson(Map<String, Object> obj) throws Exception {
            throw new UnsupportedOperationException("La deserializacion a la entidad CommercialBankExchangeRate no esta soportada");
        }
    }

    public static List<CommercialBankExchangeRate> listAll() {
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b");
    }

    public static CommercialBankExchangeRate findById(Integer id) {
        PanacheQuery<CommercialBankExchangeRate> query = find("SELECT c FROM CommercialBankExchangeRate c " +
                "JOIN FETCH c.currency curr JOIN FETCH c.bank b WHERE c.id = ?1", id);
        return query.firstResultOptional().orElseThrow(() -> EntityNotFound.create(ENTITY_NAME, id));
    }

    public static List<CommercialBankExchangeRate> findByDate(LocalDate date) {
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
                "WHERE c.date = ?1", date);
    }

    public static List<CommercialBankExchangeRate> findByDateBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw InvalidRangeException.create(start, end);
        }
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
                "WHERE c.date BETWEEN ?1 AND ?2", start, end);
    }

    public static List<CommercialBankExchangeRate> findByPeriod(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return findByDateBetween(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public static List<CommercialBankExchangeRate> findByPeriod(YearMonth yearMonth) {
        return findByDateBetween(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public static List<CommercialBankExchangeRate> findByBank(String bank) {
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
                "WHERE b.description.shortDescription = ?1", bank);
    }

    public static List<CommercialBankExchangeRate> findByBankAndDate(String bank, LocalDate date) {
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
                "WHERE b.description.shortDescription = ?1 AND c.date = ?2", bank, date);
    }

    public static List<CommercialBankExchangeRate> findByBankAndDateBetween(String bank, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw InvalidRangeException.create(start, end);
        }
        return list("SELECT c FROM CommercialBankExchangeRate c JOIN FETCH c.currency curr JOIN FETCH c.bank b " +
                "WHERE b.description.shortDescription = ?1 AND c.date BETWEEN ?2 AND ?3", bank, start, end);
    }

    public static List<CommercialBankExchangeRate> findByBankAndPeriod(String bank, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return findByBankAndDateBetween(bank, yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    @Override
    public String toString() {
        return "CommercialBankExchangeRate{" +
                "currency=" + currency +
                ", bank=" + bank +
                ", date=" + date +
                ", sell=" + sell +
                ", buy=" + buy +
                ", bestSellPrice=" + bestSellPrice +
                ", bestBuyPrice=" + bestBuyPrice +
                '}';
    }
}
