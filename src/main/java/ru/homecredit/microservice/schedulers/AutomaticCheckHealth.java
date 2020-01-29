package ru.homecredit.microservice.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.homecredit.microservice.client.CacheService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Slf4j
@Component
public class AutomaticCheckHealth {
    private final CacheService cacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private Long i = 1L;

    public String getRequestFromCache(String env, String service, String method) {
        String response = cacheService.getFromCache(env, service, method);

        if (isNull(response)) {
            String cachedString;
            try {
                cachedString = cacheService.populateCache(env, service, method);
                log.info("Populating cache: {}", cachedString);
            } catch (RestClientException e) {
                cacheService.cleanCacheByKey(env + ":" + service + "/" + method);
                cachedString = e.getMessage();
            }
            return cachedString;
        }
        log.info("From cache: {}", response);
        return response;
    }

    //todo добавить время бекапа?
    @Scheduled(cron = "0 */10 * * * ?")
    public void refreshCache() {
        Set<String> redisKeys = redisTemplate.keys("*");                                                                    // нахходим все ключи из редиски
        if (redisKeys != null && !redisKeys.isEmpty()) {                                                                            // если там не пусто
            List<String> keysList = new ArrayList<>((redisKeys));                                                                   // преобразуем множество из ключей в лист

            for (String s : keysList) {
                String[] split = s.split("[:/]");                                                                             // разделяем ключ на массивы

                List<String> cacheList = new ArrayList<>(Arrays.asList(split).subList(2, split.length));                             //берем только то что нам нужно из ключа и суем в список

                if (i % 6 == 0) {                                                                                                  // если прошел час
                    log.info("Saving to file...");
                    String cache = getRequestFromCache(cacheList.get(0), cacheList.get(1), cacheList.get(2));                        // берем ответ по ключу

                    File file = new File("src/main/resources/dump.txt");
                    try (FileWriter fr = new FileWriter(file, true)) {
                        fr.write((cacheList.get(0) + " " + cacheList.get(1) + " " + cacheList.get(2) + ":" + cache + "\n"));         // и сохраняем в файл
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cacheService.cleanCacheByKey(cacheList.get(0) + ":" + cacheList.get(1) + "/" + cacheList.get(2));           // и удаляем эту запись из кеша
                } else {
                    cacheService.populateCache(cacheList.get(0), cacheList.get(1), cacheList.get(2));                                 // если час не прошел то обновляем кеш по ключу
                }

            }
        } else log.info("Cache pull is empty");
        i++;
    }
}
