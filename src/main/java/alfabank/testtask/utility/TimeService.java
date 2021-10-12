package alfabank.testtask.utility;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
* Вспомогательный класс для работы с датами
*/
@Service
public class TimeService {
    private static final Logger logger = Logger.getLogger(TimeService.class);

    /**
    * Метод возвращает вчерашнюю дату
    */
    public String getHistoricalDate(){
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
    * Метод сравнивает текущее время и время переданное в параметр
    * @param timestamp - время в миллисекундах
    */
    public boolean compareDate(long timestamp){
        LocalDateTime previousDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        LocalDateTime currentDate = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        long minutes = Duration.between(previousDate,currentDate).toMinutes();
        logger.info(minutes+" прошло с момента последнего обновления кусов");
        return minutes > 60;
    }
}
