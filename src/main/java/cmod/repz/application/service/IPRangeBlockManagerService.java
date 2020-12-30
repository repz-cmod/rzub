package cmod.repz.application.service;

import cmod.repz.application.database.entity.repz.IPRangeBlockEntity;
import cmod.repz.application.database.repository.repz.IPRangeBlockRepository;
import cmod.repz.application.util.MathUtil;
import com.google.common.net.InetAddresses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IPRangeBlockManagerService {
    private final IPRangeBlockRepository ipRangeBlockRepository;


    @Autowired
    public IPRangeBlockManagerService(IPRangeBlockRepository ipRangeBlockRepository) {
        this.ipRangeBlockRepository = ipRangeBlockRepository;
    }


    public int count(){
        return (int) ipRangeBlockRepository.count();
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpired(){
        log.debug("Running ipb clean task");
        ipRangeBlockRepository.deleteAllByExpirationBefore(new Date());
    }

    public void add(IPRangeBlockEntity ipRangeBlockEntity){
        try {
            ipRangeBlockRepository.save(ipRangeBlockEntity);
        }catch (DataIntegrityViolationException ignored){
            log.error("Failed to insert new ip range ban");
        }
    }

    public void remove(int id){
        ipRangeBlockRepository.deleteById(id);
    }


    public List<IPRangeBlockEntity> get(int page){
        return ipRangeBlockRepository.findAll(PageRequest.of(page, 10)).getContent();
    }

    public boolean shouldBlock(String ip){
        long ipToLong = MathUtil.ipToLong(InetAddresses.forString(ip));
        return ipRangeBlockRepository.existsByRange(ipToLong);
    }

}
