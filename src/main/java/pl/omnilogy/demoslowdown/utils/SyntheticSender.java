package pl.omnilogy.demoslowdown.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.omnilogy.demoslowdown.service.PropertiesService;

import java.math.BigDecimal;
import java.util.LinkedList;


@Component
@RequiredArgsConstructor
@Slf4j
public class SyntheticSender {

    private final RestTemplate restTemplate;
    private final PropertiesService propertiesService;

    private static BigDecimal averageResponseTime = new BigDecimal(0);

    private static final LinkedList<BigDecimal> responseTimes = new LinkedList<>();
    private static final int MAX_RESPONSE_TIMES = 10;

    private static final String URL = "http://localhost:8080/upload";

    void send() {
        log.info("Sending synthetic data to {}", URL);

        byte[] content = new byte[propertiesService.getSizeInKb() * 1024];

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return "synthetic-file-" + System.currentTimeMillis() + ".dat";
            }
        };

        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        long start = System.currentTimeMillis();
        restTemplate.postForEntity(URL, requestEntity, Void.class);
        long end = System.currentTimeMillis();

        BigDecimal responseTime = new BigDecimal(end - start);
        updateResponseTimes(responseTime);

        log.info("Request completed in {} ms, avg response time: {} ms",
                responseTime, averageResponseTime);
    }

    private synchronized void updateResponseTimes(BigDecimal responseTime) {
        responseTimes.add(responseTime);
        if (responseTimes.size() > MAX_RESPONSE_TIMES) {
            responseTimes.removeFirst();
        }
        updateAverageResponseTime();
    }

    private void updateAverageResponseTime() {
        BigDecimal total = responseTimes.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int size = responseTimes.size();
        if (size == 0) {
            averageResponseTime = BigDecimal.ZERO;
        } else {
            averageResponseTime = total.divide(new BigDecimal(size), BigDecimal.ROUND_HALF_UP);
        }
    }

    public static BigDecimal getAverageResponseTime() {
        return averageResponseTime;
    }
}