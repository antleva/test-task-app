package alfabank.testtask.service.openfeignservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(value = "giphyFeignClient", url = "${giphy_api_url}")
public interface GiphyFeignClient {

    @GetMapping(value = "?api_key=${giphy_api_key}&tag={tag}&rating=g")
    Optional<String> getGif(@PathVariable String tag);
}
