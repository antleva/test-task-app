package alfabank.testtask.controllers;

import alfabank.testtask.service.exchangeratesservice.ExchangeRatesService;
import alfabank.testtask.service.giphyservice.GiphyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class ExchangeRateController {

    private ExchangeRatesService exchangeRatesService;
    private GiphyService giphyService;

    @Autowired
    public ExchangeRateController(ExchangeRatesService exchangeRatesService, GiphyService giphyService) {
        this.exchangeRatesService = exchangeRatesService;
        this.giphyService = giphyService;
    }

    @GetMapping("/")
    public String getAllCodes(Model model){
        Set<String> codes = exchangeRatesService.latest().getRates().keySet();
        model.addAttribute("codes",codes);
        return "index";
    }

    /**
     * endpoint, куда передается код валюты и возвращается гифка
     */
    @GetMapping("/crossCourse/{code}")
    public String getGif(@PathVariable String code, Model model){
        String url="";
        Set<String> codes = exchangeRatesService.latest().getRates().keySet();

        code=code.toUpperCase();
        if(!codes.contains(code)){
            model.addAttribute("message","Несуществующий код валюты");
            model.addAttribute("codes",codes);
            return "index";
        }
        int result = exchangeRatesService.crossCourse(code);

        if(result > 0){
            url = giphyService.getGifUrl("rich");
        }else if(result < 0){
            url = giphyService.getGifUrl("broke");
        }else {
            model.addAttribute("message","Курс не изменился");
        }

        model.addAttribute("codes",codes);
        model.addAttribute("url",url);

        return "index";
    }
}
