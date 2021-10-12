package alfabank.testtask.controllers;

import alfabank.testtask.domain.ExchangeRate;
import alfabank.testtask.service.exchangeratesservice.ExchangeRatesService;
import alfabank.testtask.service.giphyservice.GiphyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @MockBean
    private ExchangeRatesService exchangeRatesService;

    @MockBean
    private GiphyService giphyService;

    @Autowired
    private MockMvc mockMvc;

    private ExchangeRate latest;

    @BeforeEach
    void setUp() {
        latest = new ExchangeRate();
        latest.setTimestamp(Instant.now().getEpochSecond());
        latest.setRates(Map.of("RUB", BigDecimal.valueOf(712345, 4),
                "AFN", BigDecimal.valueOf(912345, 4)));

    }

    @Test
    void getAllCodesTest() {
        when(exchangeRatesService.latest()).thenReturn(latest);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("codes"))
                    .andExpect(model().attribute("codes", Set.of("RUB", "AFN")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getRichGifByCode() {
        when(exchangeRatesService.latest()).thenReturn(latest);
        when(exchangeRatesService.crossCourse(anyString())).thenReturn(1);
        when(giphyService.getGifUrl("rich")).thenReturn("richGifUrl");
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/crossCourse/AFN"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("codes"))
                    .andExpect(model().attributeExists("url"))
                    .andExpect(model().attribute("codes", Set.of("RUB", "AFN")))
                    .andExpect(model().attribute("url", "richGifUrl"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void getBrokeGifByCode() {
        when(exchangeRatesService.latest()).thenReturn(latest);
        when(exchangeRatesService.crossCourse(anyString())).thenReturn(-1);
        when(giphyService.getGifUrl("broke")).thenReturn("brokeGifUrl");
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/crossCourse/AFN"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("codes"))
                    .andExpect(model().attributeExists("url"))
                    .andExpect(model().attribute("codes", Set.of("RUB", "AFN")))
                    .andExpect(model().attribute("url", "brokeGifUrl"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    void getMessageWhenCourseIsNotChanged(){
        when(exchangeRatesService.crossCourse(anyString())).thenReturn(0);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/crossCourse/AFN"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("codes"))
                    .andExpect(model().attributeExists("message"))
                    .andExpect(model().attributeDoesNotExist("url"))
                    .andExpect(model().attribute("codes", Set.of("RUB", "AFN")))
                    .andExpect(model().attribute("message", "Курс не изменился"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getMessageWhenIncorrectCode() {
        when(exchangeRatesService.latest()).thenReturn(latest);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/crossCourse/MVR"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(model().attributeExists("codes"))
                    .andExpect(model().attributeExists("message"))
                    .andExpect(model().attributeDoesNotExist("url"))
                    .andExpect(model().attribute("codes", Set.of("RUB", "AFN")))
                    .andExpect(model().attribute("message", "Несуществующий код валюты"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}















