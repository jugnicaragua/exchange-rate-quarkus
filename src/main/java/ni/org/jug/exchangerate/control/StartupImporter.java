package ni.org.jug.exchangerate.control;

import io.quarkus.runtime.StartupEvent;
import ni.jug.exchangerate.ExchangeRateException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author aalaniz
 */
@Dependent
public class StartupImporter {
    private static final Logger LOGGER = Logger.getLogger(StartupImporter.class);

    @ConfigProperty(name = "exchange-rate-client.max-retry", defaultValue = "3")
    Integer maxRetryCount;

    @Inject
    ExchangeRateImporter importer;

    void startup(@Observes StartupEvent event) {
        doImportCentralBankData();
        doImportCommercialBankData();
    }
    
    private void doImportCentralBankData() {
        int count = 1;
        boolean error = true;

        do {
            try {
                importer.importHistoricalCentralBankDataUntilNow();
                error = false;
            } catch (ExchangeRateException ex) {
                LOGGER.debug("Ocurrio un error durante la importacion de los datos historicos del BCN", ex);
                LOGGER.debugf("Importacion de Datos: intento %d de %d", count, maxRetryCount);
            }
        } while (error && ++count <= maxRetryCount);

        LocalDate now = LocalDate.now();
        LocalDate referenceDate = now.withDayOfMonth(25);
        if (now.compareTo(referenceDate) >= 0) {
            try {
                importer.importCentralBankDataForNextPeriod();
            } catch (ExchangeRateException ex) {
                LOGGER.error("Ocurrio un error durante la importacion de las tasas del BCN del proximo periodo", ex);
            }
        }
    }

    private void doImportCommercialBankData() {
        importer.importListOfSupportedBanks();

        try {
            importer.importCurrentCommercialBankData();
        } catch (IllegalArgumentException ex) {
            LOGGER.errorf(ex,"Ocurrio un error durante la importacion de la compra/venta del dia de hoy [%s]", LocalDate.now());
        }
    }
}
