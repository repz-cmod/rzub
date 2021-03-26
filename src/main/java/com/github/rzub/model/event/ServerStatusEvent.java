package com.github.rzub.model.event;

import com.github.rzub.model.Iw4adminApiModel;
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
