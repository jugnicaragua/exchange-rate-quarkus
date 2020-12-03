package ni.org.jug.exchangerate.control;

import io.quarkus.scheduler.Scheduled;
import ni.jug.exchangerate.CentralBankScraper;
import ni.jug.exchangerate.CommercialBank;
import ni.jug.exchangerate.ExchangeRateClient;
import ni.jug.exchangerate.ExchangeRateException;
import ni.jug.exchangerate.ExchangeRateTrade;
import ni.jug.exchangerate.ExecutionContext;
import ni.jug.exchangerate.MonthlyExchangeRate;
import ni.org.jug.exchangerate.entity.Bank;
import ni.org.jug.exchangerate.entity.CentralBankExchangeRate;
import ni.org.jug.exchangerate.entity.CommercialBankExchangeRate;
import ni.org.jug.exchangerate.entity.Cookie;
import ni.org.jug.exchangerate.entity.Currency;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author aalaniz
 */
@ApplicationScoped
@Transactional
public class ExchangeRateImporter {

    private static final Logger LOGGER = Logger.getLogger(ExchangeRateImporter.class);

    @Inject
    EntityManager em;

    Currency dollar;

    @PostConstruct
    public void onInit() {
        dollar = Currency.findDollar();
    }

    public void importHistoricalCentralBankDataUntilNow() throws ExchangeRateException {
        YearMonth processingPeriod = YearMonth.of(CentralBankScraper.MINIMUM_YEAR, 1);
        YearMonth endPeriod = YearMonth.now();

        if (isCentralBankDataImported(processingPeriod, endPeriod)) {
            return;
        }

        List<LocalDate> datesImported = getCentralBankImportedDates();
        while (processingPeriod.compareTo(endPeriod) <= 0) {
            LOGGER.infof("BCN - Importando periodo %s", processingPeriod);

            MonthlyExchangeRate monthlyExchangeRate = ExchangeRateClient.getOfficialMonthlyExchangeRate(processingPeriod);
            List<Map.Entry<LocalDate, BigDecimal>> exchangeRateDataToImport = monthlyExchangeRate.getMonthlyExchangeRate()
                    .entrySet()
                    .stream()
                    .filter(entry -> !datesImported.contains(entry.getKey()))
                    .collect(Collectors.toList());
            for (Map.Entry<LocalDate, BigDecimal> exchangeRate : exchangeRateDataToImport) {
                CentralBankExchangeRate centralBankExchangeRate = CentralBankExchangeRate.createOf(dollar, exchangeRate.getKey(),
                        exchangeRate.getValue());
                centralBankExchangeRate.persist();
            }

            processingPeriod = processingPeriod.plusMonths(1);
        }
    }

    private boolean isCentralBankDataImported(YearMonth period) {
        return isCentralBankDataImported(period, period);
    }

    private boolean isCentralBankDataImported(YearMonth startPeriod, YearMonth endPeriod) {
        LocalDate start = startPeriod.atDay(1);
        LocalDate end = endPeriod.atEndOfMonth();

        long elapsedDays = ChronoUnit.DAYS.between(start, end) + 1;
        long recordCount = CentralBankExchangeRate.countByDateBetween(start, end);
        boolean imported = elapsedDays == recordCount;

        if (imported) {
            LOGGER.infof("BCN - %d tasas de cambio desde %s hasta %s fueron importadas en una ejecucion previa", elapsedDays,
                    start, end);
        }

        return imported;
    }

    private List<LocalDate> getCentralBankImportedDates() {
        return em.createQuery("SELECT date FROM CentralBankExchangeRate ORDER BY date", LocalDate.class).getResultList();
    }

    public void importListOfSupportedBanks() {
        Map<String, Bank> bankByShortName = getBanksMapById(false);

        for (CommercialBank commercialBank : ExchangeRateClient.commercialBanksCatalogue()) {
            Bank bank;

            if (bankByShortName.containsKey(commercialBank.getId())) {
                LOGGER.infof("Banco Comercial %s - Actualizando catalogo", commercialBank.getId());

                bank = bankByShortName.get(commercialBank.getId());
                bank.getDescription().setDescription(commercialBank.getDescription());
                bank.setUrl(commercialBank.getUrl());
                bank.setActive(Boolean.TRUE);
            } else {
                LOGGER.infof("Banco Comercial %s - Importando catalogo", commercialBank.getId());

                bank = Bank.createOf(commercialBank.getId(), commercialBank.getDescription(), commercialBank.getUrl());
                bank.persist();
            }
        }

        List<String> librarySupportedBanks = ExchangeRateClient.commercialBanksCatalogue().stream()
                .map(CommercialBank::getId)
                .collect(Collectors.toList());
        List<Map.Entry<String, Bank>> banksToDisable = bankByShortName.entrySet().stream()
                .filter(entry -> !librarySupportedBanks.contains(entry.getKey()))
                .collect(Collectors.toList());
        for (Map.Entry<String, Bank> bankEntry : banksToDisable) {
            LOGGER.infof("Inactivando Banco Comercial %s - La libreria de scraping ya no posee el scraper de este banco",
                    bankEntry.getKey());

            Bank bank = bankEntry.getValue();
            bank.setActive(Boolean.FALSE);
        }
    }

    private Map<String, Bank> getBanksMapById(boolean loadCookies) {
        List<Bank> banks = loadCookies ? Bank.listAll() : Bank.findAll().list();
        return banks.stream()
                .collect(Collectors.toMap(bank -> bank.getDescription().getShortDescription(), bank -> bank));
    }

    @Scheduled(cron = "0 15 6,16,22 21-31 * ?")
    void importCentralBankDataForNextPeriod() throws ExchangeRateException {
        YearMonth nextPeriod = YearMonth.now().plusMonths(1);

        if (isCentralBankDataImported(nextPeriod)) {
            return;
        }

        MonthlyExchangeRate monthlyExchangeRate = ExchangeRateClient.getOfficialMonthlyExchangeRate(nextPeriod);

        LOGGER.infof("BCN - Importando proximo periodo %s", nextPeriod);

        for (Map.Entry<LocalDate, BigDecimal> exchangeRate : monthlyExchangeRate) {
            CentralBankExchangeRate centralBankExchangeRate = CentralBankExchangeRate.createOf(dollar, exchangeRate.getKey(),
                    exchangeRate.getValue());
            centralBankExchangeRate.persist();
        }
    }

    @Scheduled(cron = "15 0 4,6,16,20 * * ?")
    void importCurrentCommercialBankData() {
        Map<String, Bank> banksGroupedById = getBanksMapById(true);
        List<CommercialBankExchangeRate> previouslyImportedData = CommercialBankExchangeRate.findByDate(LocalDate.now());
        Map<String, CommercialBankExchangeRate> previouslyImportedDataByBankId = previouslyImportedData.stream()
                .collect(Collectors.toMap(c -> c.getBank().getDescription().getShortDescription(), Function.identity()));

        for (ExchangeRateTrade trade : currentCommercialBankTrades(banksGroupedById, previouslyImportedData)) {
            CommercialBankExchangeRate cbExchangeRate;

            if (!previouslyImportedDataByBankId.containsKey(trade.bank())) {
                LOGGER.infof("Banco Comercial - Importando los datos de compra/venta de %s", trade.bank());

                cbExchangeRate = new CommercialBankExchangeRate();
                cbExchangeRate.setCurrency(dollar);
                cbExchangeRate.setBank(banksGroupedById.get(trade.bank()));
                cbExchangeRate.setDate(trade.date());
                cbExchangeRate.setSell(trade.sell());
                cbExchangeRate.setBuy(trade.buy());
                cbExchangeRate.setBestSellPrice(trade.isBestSellPrice());
                cbExchangeRate.setBestBuyPrice(trade.isBestBuyPrice());

                cbExchangeRate.persist();
            } else {
                LOGGER.infof("Banco Comercial - Actualizando los datos de compra/venta de %s", trade.bank());

                cbExchangeRate = previouslyImportedDataByBankId.get(trade.bank());
                cbExchangeRate.setSell(trade.sell());
                cbExchangeRate.setBuy(trade.buy());
                cbExchangeRate.setBestSellPrice(trade.isBestSellPrice());
                cbExchangeRate.setBestBuyPrice(trade.isBestBuyPrice());
            }
        }
    }

    private List<ExchangeRateTrade> currentCommercialBankTrades(Map<String, Bank> banksGroupedById,
            List<CommercialBankExchangeRate> previouslyImportedData) {
        for (Map.Entry<String, Bank> bankEntry : banksGroupedById.entrySet()) {
            for (Cookie cookie : bankEntry.getValue().getCookies()) {
                ExecutionContext.getInstance().addOrReplaceCookie(bankEntry.getKey(), cookie.getName(), cookie.getValue());
            }
        }

        List<ExchangeRateTrade> currentData = ExchangeRateClient.getCommercialBankTrades();
        List<ExchangeRateTrade> mergedData = new ArrayList<>(currentData);

        for (CommercialBankExchangeRate dbTrade : previouslyImportedData) {
            ExchangeRateTrade trade = new ExchangeRateTrade(dbTrade.getBank().getDescription().getShortDescription(), dbTrade.getDate(),
                    dbTrade.getBuy(), dbTrade.getSell());
            if (!mergedData.contains(trade)) {
                LOGGER.infof("Agregando los datos del banco %s a los resultados devueltos por el scraper", trade.bank());

                mergedData.add(trade);
            }
        }

        return ExchangeRateClient.recalculateBestOptions(mergedData);
    }
}
