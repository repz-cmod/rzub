package cmod.repz.application.service;

import cmod.repz.application.database.entity.repz.IPRegionBanEntity;
import cmod.repz.application.database.repository.repz.IPRegionBanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
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
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, duration);
        Date expiration = c.getTime();
        log.info("expiration: " + expiration);
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
    @Transactional
    public void deleteExpired(){
        log.debug("Running ipb2 clean task");
        ipRegionBanRepository.deleteAllByExpirationBefore(new Date());
    }

    public void add(IPRegionBanEntity ipRegionBanEntity){
        try {
            ipRegionBanRepository.save(ipRegionBanEntity);
        }catch (Exception e){
            log.error("Failed to insert new ip range ban");
        }
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
