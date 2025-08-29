package com.bfhl.javaapp.service;
import com.bfhl.javaapp.config.AppProperties;
import com.bfhl.javaapp.dto.GenerateWebhookResponse;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*; import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.*; import java.nio.charset.StandardCharsets; import java.nio.file.*;

import java.util.HashMap; import java.util.Map;
@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
    private final RestTemplate restTemplate; private final AppProperties props;
    public StartupRunner(RestTemplate restTemplate, AppProperties props) { this.restTemplate = restTemplate; this.props = props; }
    @Override public void run(String... args) {
        try {
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> request = new HashMap<>();
            request.put("name", props.getName()); request.put("regNo", props.getRegNo()); request.put("email", props.getEmail());
            HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,String>> entity = new HttpEntity<>(request, headers);
            log.info("Calling generateWebhook for regNo={} ...", props.getRegNo());
            ResponseEntity<GenerateWebhookResponse> resp = restTemplate.postForEntity(generateUrl, entity, GenerateWebhookResponse.class);
            GenerateWebhookResponse body = resp.getBody();
            if (body == null || body.getAccessToken() == null) throw new IllegalStateException("Invalid response from generateWebhook: " + resp);
            log.info("Received webhook response: {}", body);
            String accessToken = body.getAccessToken(); String returnedWebhook = body.getWebhook();

            String regDigits = props.getRegNo() != null ? props.getRegNo().replaceAll("\\D", "") : "";
            if (regDigits.isEmpty()) throw new IllegalArgumentException("regNo has no digits: " + props.getRegNo());
            int lastTwo = (regDigits.length() >= 2) ? Integer.parseInt(regDigits.substring(regDigits.length()-2)) : Integer.parseInt(regDigits);
            boolean isOdd = (lastTwo % 2 == 1);
            String sqlPath = isOdd ? "queries/q1.sql" : "queries/q2.sql";
            String finalQuery = readResourceToString(sqlPath);

            Path outDir = Path.of("target", "output"); Files.createDirectories(outDir);
            Path outFile = outDir.resolve("final_query.sql"); Files.writeString(outFile, finalQuery, StandardCharsets.UTF_8);
            log.info("Saved final query to {}", outFile.toAbsolutePath());

            String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            HttpHeaders submitHeaders = new HttpHeaders(); submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            if (props.isAuthBearer()) submitHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            else submitHeaders.set(HttpHeaders.AUTHORIZATION, accessToken);
            Map<String, String> submitBody = new HashMap<>(); submitBody.put("finalQuery", finalQuery);
            HttpEntity<Map<String,String>> submitEntity = new HttpEntity<>(submitBody, submitHeaders);
            ResponseEntity<String> submitResp = restTemplate.postForEntity(submitUrl, submitEntity, String.class);
            log.info("Submission (testWebhook) status={} body={}", submitResp.getStatusCode(), submitResp.getBody());

            if (props.isAlsoPostToReturnedWebhook() && returnedWebhook != null && !returnedWebhook.isBlank()) {
                try {
                    HttpHeaders wh = new HttpHeaders(); wh.setContentType(MediaType.APPLICATION_JSON);
                    if (props.isAuthBearer()) wh.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                    else wh.set(HttpHeaders.AUTHORIZATION, accessToken);
                    HttpEntity<Map<String,String>> whEntity = new HttpEntity<>(submitBody, wh);
                    ResponseEntity<String> whResp = restTemplate.postForEntity(returnedWebhook, whEntity, String.class);
                    log.info("Submission (returned webhook) status={} body={}", whResp.getStatusCode(), whResp.getBody());
                } catch (RestClientException e) { log.warn("Failed to POST to returned webhook URL: {}", e.getMessage()); }
            }
        } catch (Exception e) { log.error("Startup flow failed: {}", e.getMessage(), e); }
    }
    private String readResourceToString(String classpathResource) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) throw new FileNotFoundException("Resource not found: " + classpathResource);
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }
}
