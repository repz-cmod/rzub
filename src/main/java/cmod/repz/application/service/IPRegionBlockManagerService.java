package cmod.repz.application.service;

import cmod.repz.application.database.entity.repz.IPRegionBanEntity;
import cmod.repz.application.database.repository.repz.IPRegionBanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IPRegionBlockManagerService {
    private final IPRegionBanRepository ipRegionBanRepository;
    private final IPApiService ipApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public IPRegionBlockManagerService(IPRegionBanRepository ipRegionBanRepository, IPApiService ipApiService) {
        this.ipRegionBanRepository = ipRegionBanRepository;
        this.ipApiService = ipApiService;
    }

    public String add(String ip, String reason, String username, int duration) throws JsonProcessingException {
        IPApiService.IpInfo info = ipApiService.getInfo(ip);
        String data = objectMapper.writeValueAsString(info);
        Date date = new Date();
        Date expiration = new Date(date.getTime() + (duration * 24 * 60 * 60 * 1000));
        add(IPRegionBanEntity.builder()
                .blockHash(info.hashCode())
                .expiration(expiration)
                .creation(date)
                .reason(reason)
                .username(username)
                .info(data)
                .build());
        return data;
    }

    public int count(){
        return (int) ipRegionBanRepository.count();
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpired(){
        ipRegionBanRepository.deleteAllByExpirationBefore(new Date());
    }

    public void add(IPRegionBanEntity ipRegionBanEntity){
        try {
            ipRegionBanRepository.save(ipRegionBanEntity);
        }catch (DataIntegrityViolationException ignored){}
    }

    public void remove(int id){
        ipRegionBanRepository.deleteById(id);
    }


    public List<IPRegionBanEntity> get(int page){
        return ipRegionBanRepository.findAll(PageRequest.of(page, 10)).getContent();
    }

    public boolean shouldBlock(String ip){
        IPApiService.IpInfo info = ipApiService.getInfo(ip);
        return ipRegionBanRepository.existsByBlockHash(info.hashCode());
    }

}
