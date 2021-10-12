package alfabank.testtask.service.exchangeratesservice;

import alfabank.testtask.domain.ExchangeRate;
import alfabank.testtask.service.openfeignservice.OpenExchangeFeignClient;
import alfabank.testtask.utility.TimeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
  Сервис для получения курсов валют с https://openexchangerates.org
 */
@Service
public class ExchangeRatesServiceImplementation implements ExchangeRatesService {
    private static final Logger logger = Logger.getLogger(ExchangeRatesServiceImplementation.class);

    private TimeService timeService;
    /**
     * Feign клиент
     */
    private OpenExchangeFeignClient openExchangeFeignClient;

    /**
     * Последние курсы валют
     */
    private ExchangeRate latest;

    /**
     * Вчерашние курсы валют
     */
    private ExchangeRate historical;


    @Value("${base_currency}")
    private String baseCurrency;

    @Autowired
    public ExchangeRatesServiceImplementation(OpenExchangeFeignClient openExchangeFeignClient, TimeService timeService) {
        this.openExchangeFeignClient = openExchangeFeignClient;
        this.timeService = timeService;
    }

    /**
     * Метод возвращает последние курсы валют,
     * при первом вызове происходит обращение к сервису
     * через openExchangeFeignClient и значение записывается в поле latest,
     * при последующих вызовах проверяется время последнего обновления курсов, т.к. обновление происходит раз в час.
     * Если прошло меньше часа, возвращается значение из поля latest,
     * в противном случае поле перезаписывается
     * @return последние курсы валют
     */
    @Override
    public ExchangeRate latest() {
        if (latest == null || timeService.compareDate(latest.getTimestamp())) {
            latest = openExchangeFeignClient.latest().orElseThrow(() -> new RuntimeException("Загрузка последних курсов валют не выполнена"));
            logger.info("Загрузка последних курсов валют");
            return latest;
        }
        return latest;
    }

    /**
     * Метод возвращает вчерашние курсы валют.
     * Значение записывается один раз и не меняется.
     * @param date - дата, для которой нужно получить курсы, передаётся в формате yyyy-mm-dd
     * @return вчерашние курсы валют
     */
    @Override
    public ExchangeRate historical(String date) {
        if (historical == null) {
            historical = openExchangeFeignClient.historical(date).orElseThrow(() -> new RuntimeException("Дата " + date + " не соответствует формату "));
            logger.info("Загрузка вчерашних курсов валют");
            return historical;
        }
        return historical;
    }
    /**
     * Метод для сравнения кросс-курсов
     * @param code - код валюты, для которой нужно рассчитать кросс-курс
     */
    @Override
    public int crossCourse(String code) {
        ExchangeRate latest = latest();
        ExchangeRate historical = historical(timeService.getHistoricalDate());

        BigDecimal latestCrossCourse = compute(latest, code);
        BigDecimal historicalCrossCourse = compute(historical, code);

        int result = latestCrossCourse.compareTo(historicalCrossCourse);
        logger.info("вчерашний курс по отношению к рублю : " + historicalCrossCourse);
        logger.info("сегодняшний курс по отношению к рублю : " + latestCrossCourse);
        logger.info("результат :" + result);
        return result;
    }
    /**
     * Метод для расчета кросс-курс рубля и выбранной валюты
     * Расчет производится по формуле:
     * A/B = USD/B: USD/A, где А – это курс рубля относительно доллара, В – курс выбранной валюты.
     * @param rates - курсы валют
     * @param code - код валюты
     * @return кросс-курс
     */
    private BigDecimal compute(ExchangeRate rates, String code) {
        BigDecimal codeRate = rates.getRates().get(code);
        BigDecimal baseRate = rates.getRates().get(baseCurrency);

        BigDecimal crossCourse = codeRate.divide(baseRate, 6, RoundingMode.HALF_UP);

        return crossCourse;
    }


}
