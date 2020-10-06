package cmod.repz.application.service;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.bo2.XlrBo2StatsRepository;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.util.OffsetLimitPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheableXlrTopStats {
    private final XlrBo2StatsRepository xlrBo2StatsRepository;
    private final XlrMw2StatsRepository xlrMw2StatsRepository;

    @Autowired
    public CacheableXlrTopStats(XlrBo2StatsRepository xlrBo2StatsRepository, XlrMw2StatsRepository xlrMw2StatsRepository) {
        this.xlrBo2StatsRepository = xlrBo2StatsRepository;
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
    }

    @Cacheable(cacheNames = "xlrTopStats", key = "#game", unless="#result == null")
    public List<XlrPlayerStatEntity> getXlrTopStats(String game){
        List<XlrPlayerStatEntity> xlrPlayerStatEntities = null;
        if(game.equals("bo2")){
            xlrPlayerStatEntities = xlrBo2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        }else if(game.equals("mw2")){
            xlrPlayerStatEntities = xlrMw2StatsRepository.findAll(new OffsetLimitPageable(0, 10, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        }

        return xlrPlayerStatEntities;
    }
}
