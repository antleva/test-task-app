package alfabank.testtask.service.openfeignservice;

import alfabank.testtask.domain.ExchangeRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(value = "openExchangeFeignClient", url = "${openexchangerates_api_url}")
public interface OpenExchangeFeignClient {

    @GetMapping(value = "latest.json?app_id=${openexchangerates_api_key}")
    Optional<ExchangeRate> latest();

    @GetMapping(value = "historical/{date}.json?app_id=${openexchangerates_api_key}")
    Optional<ExchangeRate> historical(@PathVariable String date);

}
