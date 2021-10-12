package alfabank.testtask.service.giphyservice;

import alfabank.testtask.service.openfeignservice.GiphyFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class GiphyServiceImplementationTest {

    @Autowired
    private GiphyServiceImplementation giphyServiceImplementation;

    @MockBean
    private GiphyFeignClient giphyFeignClient;

    private String response = "{" +
            " data:" +
            "     {" +
            "     images:" +
            "           {" +
            "            original:" +
            "                    {" +
            "                     url:rich" +
            "                    }" +
            "            }" +
            "     }" +
            "}";
    ;


    @Test
    void loadRichGifByTag() {
        when(giphyFeignClient.getGif(anyString())).thenReturn(Optional.of(response));
        String tag = giphyServiceImplementation.getGifUrl("rich");
        assertNotNull(tag);
        assertEquals(tag, "rich");

    }

    @Test
    void loadBrokeGifByTag() {
        when(giphyFeignClient.getGif(anyString())).thenReturn(Optional.of(response));
        String tag = giphyServiceImplementation.getGifUrl("broke");
        assertNotNull(tag);
        assertNotEquals(tag, "broke");

    }

    @Test
    void throwExceptionWhenUnableToLoadGif() {
        when(giphyFeignClient.getGif(anyString())).thenThrow(new RuntimeException("Загрузка гифки не выполнена"));
        Exception e = null;
        try {
            giphyServiceImplementation.getGifUrl("tag");
        } catch (RuntimeException r) {
            e = r;
        }
        assertNotNull(e);
        assertEquals(e.getClass(), RuntimeException.class);
        assertEquals("Загрузка гифки не выполнена", e.getMessage());
    }


}
