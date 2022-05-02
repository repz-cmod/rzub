package com.github.rzub.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rzub.database.entity.CookieEntity;
import com.github.rzub.database.entity.IW4MAdminUserEntity;
import com.github.rzub.database.repository.CookieRepository;
import com.github.rzub.model.Iw4madminApiModel;
import com.github.rzub.model.SettingsModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IW4MAdminApiService {
    private final SettingsModel settingsModel;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private volatile String cachedIw4madminUrl;
    private final String execAddress;
    private final CookieRepository cookieRepository;


    @Autowired
    public IW4MAdminApiService(@Lazy SettingsModel settingsModel, ObjectMapper objectMapper, CookieRepository cookieRepository) {
        this.settingsModel = settingsModel;
        this.objectMapper = objectMapper;
        this.cookieRepository = cookieRepository;
        this.restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        execAddress = getIw4madminUrl() + "/Console/Execute?serverId={serverId}&command={command}";
    }

    public CookieEntity logIn(String cid, String password) throws Exception {
        return this.logIn(cid, password, null);
    }

    public CookieEntity logIn(String cid, String passwd, @Nullable IW4MAdminUserEntity iw4MAdminUserEntity) throws Exception {
        String addr = getIw4madminUrl() + "/Account/Login?clientId="+cid+"&password="+passwd;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(addr, String.class);
        List<String> cookie = extractCookies(responseEntity);
        if(cookie != null){
            String s = objectMapper.writeValueAsString(cookie.toArray(new String[0]));
            return cookieRepository.save(CookieEntity.builder()
                    .iw4MAdminUser(iw4MAdminUserEntity)
                    .cookie(s)
                    .build());
        }else {
            throw new Exception("Failed to login. Null cookie");
        }

    }

    public CommandResponse execute(String serverId, String command){
        return this.execute(serverId, command, null);
    }

    public CommandResponse execute(String serverId, String command, @Nullable IW4MAdminUserEntity iw4MAdminUserEntity){
        HttpHeaders headers = getBasicHeaders();
        try {
            headers.put("cookie", getCookie(iw4MAdminUserEntity));
        } catch (Exception e) {
            return new CommandResponse(false, -1, "Failed to get cookie");
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

    public boolean sendCommand(String serverId, String command){
        return this.execute(serverId, command).isSuccess();
    }

    public boolean sendCommand(String serverId, String command, @Nullable IW4MAdminUserEntity iw4MAdminUserEntity){
        return this.execute(serverId, command, null).isSuccess();
    }

    public List<Iw4madminApiModel.Server> getServerList(){
        String iw4madminUrl = getIw4madminUrl();
        ResponseEntity<List<Iw4madminApiModel.Server>> responseEntity = restTemplate.exchange(iw4madminUrl + "/api/status",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Iw4madminApiModel.Server>>() {
                });
        return responseEntity.getBody();
    }

    public Iw4madminApiModel.FindApiResult findClient(String query){
        return restTemplate.getForEntity(getIw4madminUrl() + "/api/client/find?name="+query, Iw4madminApiModel.FindApiResult.class).getBody();
    }

    public List<Iw4madminApiModel.Stat> getClientStats(String clientId){
        ResponseEntity<List<Iw4madminApiModel.Stat>> responseEntity = restTemplate.exchange(getIw4madminUrl() + "/api/stats/"+clientId,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Iw4madminApiModel.Stat>>() {
                });
        return responseEntity.getBody();
    }

    private HttpHeaders getBasicHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("User-Agent", Collections.singletonList("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:84.0) Gecko/20100101 Firefox/84.0"));
        httpHeaders.put("Accept", Collections.singletonList("*/*"));
        httpHeaders.put("Accept-Language", Collections.singletonList("en-US,en;q=0.5"));
        httpHeaders.put("X-Requested-With", Collections.singletonList("XMLHttpRequest"));
        httpHeaders.put("Referer", Collections.singletonList(settingsModel.getIw4madminUrl()));
        httpHeaders.put("DNT", Collections.singletonList("1"));
        httpHeaders.put("Pragma", Collections.singletonList("no-cache"));
        httpHeaders.put("Cache-Control", Collections.singletonList("no-cache"));
        return httpHeaders;
    }

    private List<String> getCookie(@Nullable IW4MAdminUserEntity iw4MAdminUserEntity) throws Exception {
        Optional<CookieEntity> optionalCookieEntity;
        if (iw4MAdminUserEntity == null) {
            optionalCookieEntity = cookieRepository.findTop1ByIw4MAdminUserIsNullOrderByIdDesc();
        }else {
            optionalCookieEntity = cookieRepository.findTop1ByIw4MAdminUserOrderByIdDesc(iw4MAdminUserEntity);
        }
        if (!optionalCookieEntity.isPresent())
            throw new Exception("No cookie found");
        String[] strings = objectMapper.readValue(optionalCookieEntity.get().getCookie(), String[].class);
        return Arrays.asList(strings);
    }

    private List<String> extractCookies(ResponseEntity<String> responseEntity) {
        List<String> cookie = responseEntity.getHeaders().get("Set-Cookie");
        if(cookie == null)
            cookie = responseEntity.getHeaders().get("cookie");
        return cookie;
    }

    private String getIw4madminUrl() {
        if(cachedIw4madminUrl != null)
            return cachedIw4madminUrl;
        String iw4madminUrl = new String(this.settingsModel.getIw4madminUrl());
        if(iw4madminUrl.endsWith("/")){
            iw4madminUrl = iw4madminUrl.substring(0, iw4madminUrl.length() - 1);
        }
        cachedIw4madminUrl = iw4madminUrl;
        return iw4madminUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class CommandResponse {
        private final boolean success;
        private final int status;
        private final String body;
    }

}
