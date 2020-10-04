package cmod.repz.application.service;

import cmod.repz.application.database.entity.xlr.ClientEntity;
import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2ClientRepository;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2ClientRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.util.OffsetLimitPageable;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheableXlrTopStats {
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final XlrBo2ClientRepository xlrBo2ClientRepository;
    private final XlrMw2StatsRepository xlrMw2StatsRepository;
    private final XlrMw2ClientRepository xlrMw2ClientRepository;

    @Autowired
    public CacheableXlrTopStats(XlrBo2StatsRepository xlrBo2StatsRepository, XlrBo2ClientRepository xlrBo2ClientRepository, XlrMw2StatsRepository xlrMw2StatsRepository, XlrMw2ClientRepository xlrMw2ClientRepository) {
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.xlrBo2ClientRepository = xlrBo2ClientRepository;
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
        this.xlrMw2ClientRepository = xlrMw2ClientRepository;
    }

    @Cacheable(cacheNames = "xlrTopStats", key = "#game", unless="#result == null")
    public Result getXlrTopStats(String game){
        List<XlrPlayerStatEntity> xlrPlayerStatEntities = null;
        List<ClientEntity> clientEntities = null;
        if(game.equals("bo2")){
            xlrPlayerStatEntities = xlrBo2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
            clientEntities = xlrBo2ClientRepository.findAllById(extractClientIds(xlrPlayerStatEntities));
        }else if(game.equals("mw2")){
            xlrPlayerStatEntities = xlrMw2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
            clientEntities = xlrMw2ClientRepository.findAllById(extractClientIds(xlrPlayerStatEntities));
        }

        return Result.builder()
                .clientEntities(clientEntities)
                .xlrPlayerStatEntities(xlrPlayerStatEntities)
                .build();
    }

    private List<Integer> extractClientIds(List<XlrPlayerStatEntity> xlrPlayerStatEntities){
        List<Integer> ids = new ArrayList<>();
        xlrPlayerStatEntities.forEach(xlrPlayerStatEntity -> {
            ids.add(xlrPlayerStatEntity.getClientId());
        });

        return ids;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private List<ClientEntity> clientEntities;
        private List<XlrPlayerStatEntity> xlrPlayerStatEntities;
    }
}
