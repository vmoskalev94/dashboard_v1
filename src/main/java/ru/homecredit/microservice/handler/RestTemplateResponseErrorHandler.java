package ru.homecredit.microservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            log.warn(response.getStatusCode().toString());

        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            log.warn(response.getStatusCode().toString());

        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }
}
