package cmod.repz.application.model.event;

import cmod.repz.application.model.Iw4adminApiModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ServerStatusEvent extends ApplicationEvent {
    private final List<Iw4adminApiModel.Server> serverList;

    public ServerStatusEvent(Object source, List<Iw4adminApiModel.Server> serverList) {
        super(source);
        this.serverList = serverList;
    }
}
