package cmod.repz.application.model.event;

import cmod.repz.application.database.entity.xlr.XlrPlayerStatEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class XlrTopEvent extends ApplicationEvent {
    private final List<XlrPlayerStatEntity> mw2Stats;
    private final List<XlrPlayerStatEntity> bo2Stats;

    public XlrTopEvent(Object source, List<XlrPlayerStatEntity> mw2Stats, List<XlrPlayerStatEntity> bo2Stats) {
        super(source);
        this.mw2Stats = mw2Stats;
        this.bo2Stats = bo2Stats;
    }
}
