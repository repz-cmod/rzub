package cmod.repz.application.service.api;

import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.Iw4adminApiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class IW4AdminApi {
    private final ConfigModel configModel;
    private final RestTemplate restTemplate;
    private volatile String cachedIw4adminUrl;

    @Autowired
    public IW4AdminApi(ConfigModel configModel) {
        this.configModel = configModel;
        this.restTemplate = new RestTemplate();
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
