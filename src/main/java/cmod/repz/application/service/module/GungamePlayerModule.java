package cmod.repz.application.service.module;

import cmod.repz.application.database.entity.repz.GungamePlayerEntity;
import cmod.repz.application.database.repository.repz.GungamePlayerRepository;
import cmod.repz.application.util.OffsetLimitPageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GungamePlayerModule {
    private final GungamePlayerRepository gungamePlayerRepository;

    public GungamePlayerModule(GungamePlayerRepository gungamePlayerRepository) {
        this.gungamePlayerRepository = gungamePlayerRepository;
    }

    public String rank(int size){
        List<GungamePlayerEntity> playerEntities = gungamePlayerRepository.findAll(new OffsetLimitPageable(0, size, Sort.by(Sort.Direction.DESC, "wins"))).getContent();
        StringBuilder stringBuilder = new StringBuilder();
        playerEntities.forEach(gungamePlayerEntity -> {
            stringBuilder.append(gungamePlayerEntity.getGuid());
            stringBuilder.append("|");
        });
        return stringBuilder.toString();
    }

    public String process(String data){
        String[] split = data.split("-");
        if(split.length < 2)
            return null;

        switch (split[0]){
            case "register":
                register(split[1]);
                return "ok";
            case "increment":
                increment(split[1]);
                return "ok";
            case "read":
                return readRank(split[1]);
            case "all":
                return rank(Integer.parseInt(split[1]));
            default:
                return null;
        }
    }

    private void register(String guid){
        if(!gungamePlayerRepository.existsByGuid(guid))
            gungamePlayerRepository.save(GungamePlayerEntity.builder()
                    .guid(guid)
                    .wins(0)
                    .build());
    }

    private void increment(String guid){
        register(guid);
        gungamePlayerRepository.increaseWins(guid);
    }


    private String readRank(String guid){
        GungamePlayerEntity gungamePlayerEntity = gungamePlayerRepository.findByGuid(guid);
        if(gungamePlayerEntity == null)
            return "1000";
        return gungamePlayerRepository.countAllByWinsGreaterThan(gungamePlayerEntity.getWins()) + "|" + gungamePlayerEntity.getWins();
    }

}
