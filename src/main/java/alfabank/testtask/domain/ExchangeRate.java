package alfabank.testtask.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class ExchangeRate {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String, BigDecimal> rates;



}
