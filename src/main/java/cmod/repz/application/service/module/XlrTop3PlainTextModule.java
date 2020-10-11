package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import cmod.repz.application.database.repository.xlr.mw2.XlrMw2StatsRepository;
import cmod.repz.application.util.OffsetLimitPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class XlrTop3PlainTextModule {
    private final XlrMw2StatsRepository xlrMw2StatsRepository;

    @Autowired
    public XlrTop3PlainTextModule(XlrMw2StatsRepository xlrMw2StatsRepository) {
        this.xlrMw2StatsRepository = xlrMw2StatsRepository;
    }

    public String getTop3PlayersStats(){
        List<XlrPlayerStatEntity> xlrPlayerStatEntities = xlrMw2StatsRepository.findAll(new OffsetLimitPageable(0, 3, Sort.by(Sort.Direction.DESC, "skill"))).getContent();
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<XlrPlayerStatEntity> iterator = xlrPlayerStatEntities.iterator();
        while (iterator.hasNext()){
            stringBuilder.append(iterator.next().getClient().getGuid());
            if(iterator.hasNext())
                stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
