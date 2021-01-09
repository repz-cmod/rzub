package cmod.repz.application.service.api;

import cmod.repz.application.database.entity.repz.CookieEntity;
import cmod.repz.application.database.repository.repz.CookieRepository;
import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.Iw4adminApiModel;
import cmod.repz.application.util.OffsetLimitPageable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class IW4AdminApi {
    private final ConfigModel configModel;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private volatile String cachedIw4adminUrl;

    @Autowired
    public IW4AdminApi(ConfigModel configModel, ObjectMapper objectMapper) {
        this.configModel = configModel;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public boolean logIn(String cid, String passwd, CookieRepository cookieRepository){
        String addr = getIw4adminUrl() + "/Account/LoginAsync?clientId"+cid+"&password="+passwd;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(addr, String.class);
        List<String> cookie = getCookie(responseEntity);
        if(cookie != null){
            try {
                cookieRepository.save(CookieEntity.builder()
                        .cookie(objectMapper.writeValueAsString(cookie.toArray(new String[0])))
                        .build());
            } catch (JsonProcessingException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private List<String> getCookie(ResponseEntity<String> responseEntity) {
        List<String> cookie = responseEntity.getHeaders().get("Cookie");
        if(cookie == null)
            cookie = responseEntity.getHeaders().get("cookie");
        return cookie;
    }

    public boolean sendCommand(String serverId, String command, CookieRepository cookieRepository){
        String addr = getIw4adminUrl() + "/Console/ExecuteAsync?server"+serverId+"&command="+command;
        HttpHeaders headers = getBasicHeaders();
        try {
            headers.put("cookie", getCookie(cookieRepository));
        } catch (Exception e) {
            return false;
        }
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(addr, HttpMethod.GET, entity, String.class);
            if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
                return true;
            }
            return false;
        }catch (Exception e){
            log.error("Failed to send request", e);
            return false;
        }

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
}
