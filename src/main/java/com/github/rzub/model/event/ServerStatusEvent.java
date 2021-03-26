package com.github.rzub.model.event;

import com.github.rzub.model.Iw4madminApiModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ServerStatusEvent extends ApplicationEvent {
    private final List<Iw4madminApiModel.Server> serverList;

    public ServerStatusEvent(Object source, List<Iw4madminApiModel.Server> serverList) {
        super(source);
        this.serverList = serverList;
    }
}
