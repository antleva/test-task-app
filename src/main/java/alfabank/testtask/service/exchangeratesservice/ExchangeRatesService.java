package alfabank.testtask.service.exchangeratesservice;

import alfabank.testtask.domain.ExchangeRate;

public interface ExchangeRatesService {
    ExchangeRate latest();
    ExchangeRate historical(String date);
    int crossCourse(String code);
}
