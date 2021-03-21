package cmod.repz.application.service.api;

import cmod.repz.application.database.entity.repz.CookieEntity;
import cmod.repz.application.database.repository.repz.CookieRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.util.OffsetLimitPageable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;

@Component
@Slf4j
public class IW4AdminApi {
    private final ConfigModel configModel;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private volatile String cachedIw4adminUrl;
    private final String execAddress = getIw4adminUrl() + "/Console/ExecuteAsync?serverId={serverId}&command={command}";

    @Autowired
    public IW4AdminApi(ConfigModel configModel, ObjectMapper objectMapper) {
        this.configModel = configModel;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
    }

    public boolean logIn(String cid, String passwd, CookieRepository cookieRepository){
        String addr = getIw4adminUrl() + "/Account/LoginAsync?clientId="+cid+"&password="+passwd;
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(addr, String.class);
            List<String> cookie = getCookie(responseEntity);
            if(cookie != null){
                try {
                    String s = objectMapper.writeValueAsString(cookie.toArray(new String[0]));
                    System.out.println(s);
                    cookieRepository.save(CookieEntity.builder()
                            .cookie(s)
                            .build());
                } catch (JsonProcessingException e) {
                    log.error("Error while parsing json", e);
                    return false;
                }
                return true;
            }
        }catch (Exception e){
            log.error("Failed to send req", e);
        }

        return false;
    }

    private List<String> getCookie(ResponseEntity<String> responseEntity) {
        List<String> cookie = responseEntity.getHeaders().get("Set-Cookie");
        if(cookie == null)
            cookie = responseEntity.getHeaders().get("cookie");
        return cookie;
    }

    public CommandResponse execute(String serverId, String command, CookieRepository cookieRepository){
        HttpHeaders headers = getBasicHeaders();
        try {
            headers.put("cookie", getCookie(cookieRepository));
        } catch (Exception e) {
            return new CommandResponse(false, -1, "");
        }
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(execAddress, HttpMethod.GET, entity, String.class, serverId, command);
            return new CommandResponse(true, responseEntity.getStatusCodeValue(), responseEntity.getBody());
        }catch (Exception e){
            log.error("Failed to send request", e);
            return new CommandResponse(false, responseEntity != null ? responseEntity.getStatusCodeValue() : -1, responseEntity != null ? responseEntity.getBody() : "");
        }
    }

    public boolean sendCommand(String serverId, String command, CookieRepository cookieRepository){
        return this.execute(serverId, command, cookieRepository).isSuccess();
    }

    private HttpHeaders getBasicHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("User-Agent", Collections.singletonList("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"));
        httpHeaders.put("Accept", Collections.singletonList("*/*"));
        httpHeaders.put("Accept-Language", Collections.singletonList("en-US,en;q=0.5"));
        httpHeaders.put("X-Requested-With", Collections.singletonList("XMLHttpRequest"));
        httpHeaders.put("Referer", Collections.singletonList("http://admin.cmod.pw:8080/"));
        httpHeaders.put("DNT", Collections.singletonList("1"));
        httpHeaders.put("Pragma", Collections.singletonList("no-cache"));
        httpHeaders.put("Cache-Control", Collections.singletonList("no-cache"));
        return httpHeaders;
    }

    private List<String> getCookie(CookieRepository cookieRepository) throws Exception {
        List<CookieEntity> content = cookieRepository.findAll(new OffsetLimitPageable(0, 1, Sort.by(Sort.Direction.DESC, "id"))).getContent();
        if(content.size() > 0){
            String[] strings = objectMapper.readValue(content.get(0).getCookie(), String[].class);
            return Arrays.asList(strings);
        }
        throw new Exception("No cookie found");
    }

    public List<Iw4adminApiModel.Server> getServerList(){
        String iw4adminUrl = getIw4adminUrl();
        ResponseEntity<List<Iw4adminApiModel.Server>> responseEntity = restTemplate.exchange(iw4adminUrl + "/api/status",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Iw4adminApiModel.Server>>() {
                });
        return responseEntity.getBody();
    }

    public Iw4adminApiModel.FindApiResult findClient(String query){
        return restTemplate.getForEntity(getIw4adminUrl() + "/api/client/find?name="+query, Iw4adminApiModel.FindApiResult.class).getBody();
    }

    public List<Iw4adminApiModel.Stat> getClientStats(String clientId){
        ResponseEntity<List<Iw4adminApiModel.Stat>> responseEntity = restTemplate.exchange(getIw4adminUrl() + "/api/stats/"+clientId,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Iw4adminApiModel.Stat>>() {
                });
        return responseEntity.getBody();
    }

    private String getIw4adminUrl() {
        if(cachedIw4adminUrl != null)
            return cachedIw4adminUrl;
        String iw4adminUrl = new String(configModel.getIw4adminUrl());
        if(iw4adminUrl.endsWith("/")){
            iw4adminUrl = iw4adminUrl.substring(0, iw4adminUrl.length() - 1);
        }
        cachedIw4adminUrl = iw4adminUrl;
        return iw4adminUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class CommandResponse {
        private final boolean success;
        private final int status;
        private final String body;
    }


    public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

        final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            traceRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            traceResponse(response);
            return response;
        }

        private void traceRequest(HttpRequest request, byte[] body) throws IOException {
            log.debug("===========================request begin================================================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders() );
            log.debug("Request body: {}", new String(body, "UTF-8"));
            log.debug("==========================request end================================================");
        }

        private void traceResponse(ClientHttpResponse response) throws IOException {
            StringBuilder inputStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            log.debug("Headers      : {}", response.getHeaders());
            log.debug("Response body: {}", inputStringBuilder.toString());
            log.debug("=======================response end=================================================");
        }

    }
}
