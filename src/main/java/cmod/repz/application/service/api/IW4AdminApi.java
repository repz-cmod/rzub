package cmod.repz.application.service.api;

import cmod.repz.application.model.ConfigModel;
import cmod.repz.application.model.ServerStatusModel;
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

    @Autowired
    public IW4AdminApi(ConfigModel configModel) {
        this.configModel = configModel;
        this.restTemplate = new RestTemplate();
    }

    public List<ServerStatusModel.Server> getServerList(){
        String iw4adminUrl = new String(configModel.getIw4adminUrl());
        if(iw4adminUrl.endsWith("/")){
            iw4adminUrl = iw4adminUrl.substring(0, iw4adminUrl.length() - 1);
        }
        ResponseEntity<List<ServerStatusModel.Server>> responseEntity = restTemplate.exchange(iw4adminUrl + "/api/status",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ServerStatusModel.Server>>() {
                });
        return responseEntity.getBody();
    }
}
