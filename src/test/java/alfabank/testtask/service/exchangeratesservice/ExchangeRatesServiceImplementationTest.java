package alfabank.testtask.service.exchangeratesservice;

import alfabank.testtask.domain.ExchangeRate;
import alfabank.testtask.service.openfeignservice.OpenExchangeFeignClient;
import alfabank.testtask.utility.TimeService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ExchangeRatesServiceImplementationTest {

    @Autowired
    private ExchangeRatesServiceImplementation exchangeRatesServiceImplementation;

    @MockBean
    private OpenExchangeFeignClient openExchangeFeignClient;

    @MockBean
    private TimeService timeService;

    private ExchangeRate latest;
    private ExchangeRate historical;

    @BeforeEach
    void setUp() {
        latest = new ExchangeRate();
        latest.setTimestamp(Instant.now().getEpochSecond());
        latest.setRates(Map.of("RUB", BigDecimal.valueOf(712345,4),
                               "AFN", BigDecimal.valueOf(912345,4)));

        historical = new ExchangeRate();
        historical.setTimestamp(Instant.now().minusSeconds(3600).getEpochSecond());
        historical.setRates(Map.of("RUB", BigDecimal.valueOf(715432,4),
                               "AFN", BigDecimal.valueOf(954321,4)));


    }

    @Test
    void latest() {
        when(openExchangeFeignClient.latest()).thenReturn(Optional.of(latest));
        when(timeService.compareDate(anyLong())).thenReturn(true);

        ExchangeRate getLatest = exchangeRatesServiceImplementation.latest();

        assertNotNull(getLatest);
        verify(openExchangeFeignClient,only()).latest();
        verify(openExchangeFeignClient,times(1)).latest();
        verify(openExchangeFeignClient,never()).historical(anyString());

    }

    @Test
    void historical() {
        when(openExchangeFeignClient.historical(anyString())).thenReturn(Optional.of(historical));

        ExchangeRate getHistorical = exchangeRatesServiceImplementation.historical("2021-10-10");

        assertNotNull(getHistorical);
        verify(openExchangeFeignClient,never()).latest();
    }

    @Test
    void crossCourse() {
        when(openExchangeFeignClient.latest()).thenReturn(Optional.of(latest));
        when(openExchangeFeignClient.historical(anyString())).thenReturn(Optional.of(historical));
        when(timeService.getHistoricalDate()).thenReturn("2021-10-10");
        when(timeService.compareDate(anyLong())).thenReturn(true);

        int crossCourse = exchangeRatesServiceImplementation.crossCourse("AFN");

        verify(openExchangeFeignClient,times(1)).historical(anyString());
        verify(openExchangeFeignClient,times(1)).latest();

        assertEquals(crossCourse,-1);
    }

    @Test
    void whenUnableLoadLatestThenThrowException(){
        when(openExchangeFeignClient.latest()).thenThrow(new RuntimeException("Unable to load latest rate"));
        when(timeService.compareDate(anyLong())).thenReturn(true);

        Exception e = null;
        try {
            exchangeRatesServiceImplementation.latest();
        } catch (RuntimeException r) {
            e = r;
        }
        assertNotNull(e);
        assertEquals(e.getClass(),RuntimeException.class);
        assertEquals("Unable to load latest rate",e.getMessage());
    }

    @Test
    void whenUnableLoadHistoricalThenThrowException() {
        when(openExchangeFeignClient.historical(anyString())).thenThrow(new RuntimeException("Incorrect data"));

        Exception e = null;
        try {
            openExchangeFeignClient.historical("2021-11-11");
        } catch (RuntimeException r) {
            e = r;
        }
        assertNotNull(e);
        assertEquals(e.getClass(),RuntimeException.class);
        assertEquals("Incorrect data",e.getMessage());

    }

    /**
     * Обращения к сервису курсов валют не происходит, если поле latest не равно null
     */
    @Test
    void getLatestFromCache(){
        when(openExchangeFeignClient.latest()).thenReturn(Optional.of(latest));
        when(timeService.compareDate(anyLong())).thenReturn(false);

        ExchangeRate latest1 = exchangeRatesServiceImplementation.latest();
        ExchangeRate latest2 = exchangeRatesServiceImplementation.latest();

        assertNotNull(latest1);
        assertNotNull(latest2);
        assertSame(latest1,latest2);
        verify(openExchangeFeignClient,times(0)).historical(anyString());
        verify(openExchangeFeignClient,times(0)).latest();
    }
//
    /**
     * Обращения к сервису курсов валют не происходит, если поле historical не равно null
     */
    @Test
    void getHistoricalFromCache(){
        when(openExchangeFeignClient.historical(anyString())).thenReturn(Optional.of(historical));

        ExchangeRate historical1 = exchangeRatesServiceImplementation.historical("2021-10-10");
        ExchangeRate historical2 = exchangeRatesServiceImplementation.historical("2021-10-10");

        assertNotNull(historical1);
        assertNotNull(historical2);
        assertSame(historical1,historical2);
        verify(openExchangeFeignClient,times(0)).latest();
        verify(openExchangeFeignClient,times(0)).historical(anyString());
    }
}