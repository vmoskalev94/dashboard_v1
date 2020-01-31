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

    /**
     * Порядок действий:
     * нахходим все ключи из редиски
     * если там не пусто
     * преобразуем множество из ключей в лист
     * разделяем ключ на массивы
     * берем только то что нам нужно из ключа и суем в список
     * если прошел час
     * берем ответ по ключу
     * и сохраняем в файл
     * и удаляем эту запись из кеша
     * если час не прошел то обновляем кеш по ключу
     */
    //todo добавить время бекапа?
    @Scheduled(cron = "0 */10 * * * ?")
    public void refreshCache() {
        Set<String> redisKeys = redisTemplate.keys("*");
        if (redisKeys != null && !redisKeys.isEmpty()) {
            List<String> keysList = new ArrayList<>((redisKeys));

            for (String s : keysList) {
                String[] split = s.split("[:/]");

                List<String> cacheList = new ArrayList<>(Arrays.asList(split).subList(2, split.length));

                if (i % 6 == 0) {
                    log.info("Saving to file...");
                    String cache = getRequestFromCache(cacheList.get(0), cacheList.get(1), cacheList.get(2));

                    File file = new File("src/main/resources/dump.txt");
                    try (FileWriter fr = new FileWriter(file, true)) {
                        fr.write((cacheList.get(0) + " " + cacheList.get(1) + " " + cacheList.get(2) + ":" + cache + "\n"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cacheService.cleanCacheByKey(cacheList.get(0) + ":" + cacheList.get(1) + "/" + cacheList.get(2));
                } else {
                    cacheService.populateCache(cacheList.get(0), cacheList.get(1), cacheList.get(2));
                }

            }
        } else log.info("Cache pull is empty");
        i++;
    }
}
