package alfabank.testtask.service.giphyservice;

import alfabank.testtask.service.openfeignservice.GiphyFeignClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для загрузки гифки из https://api.giphy.com
 */
@Service
public class GiphyServiceImplementation implements GiphyService {
    private static final Logger logger = Logger.getLogger(GiphyServiceImplementation.class);

    /**
     * Feign клиент
     */
    private GiphyFeignClient giphyFeignClient;

    @Autowired
    public GiphyServiceImplementation(GiphyFeignClient giphyFeignClient) {
        this.giphyFeignClient = giphyFeignClient;
    }

    /**
     * Метод загружает гифку по тэгу в формате JSON и возвращает URL для отрисовки гифки на странице
     * @param tag - тэг в виде строки
     * @return  url гифки в виде строки
     */
    @Override
    public String getGifUrl(String tag) {
        String jsonString = giphyFeignClient.getGif(tag).orElseThrow(()->new RuntimeException("Загрузка гифки не выполнена"));
        JSONObject root = new JSONObject(jsonString);
        JSONObject data = (JSONObject)root.get("data");
        JSONObject images = (JSONObject)data.get("images");
        String gifUrl = images.getJSONObject("original").getString("url");
        logger.info("Загрузка гифки по тэгу "+tag);
        logger.info("===========================");
        return gifUrl;
    }
}

