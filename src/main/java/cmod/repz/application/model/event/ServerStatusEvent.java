package cmod.repz.application.model.event;

import cmod.repz.application.model.ServerStatusModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ServerStatusEvent extends ApplicationEvent {
    private final List<ServerStatusModel.Server> serverList;

    public ServerStatusEvent(Object source, List<ServerStatusModel.Server> serverList) {
        super(source);
        this.serverList = serverList;
    }
}
