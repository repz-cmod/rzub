package cmod.repz.application.service.module;

import cmod.repz.application.model.event.ServerStatusEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PlayerInformationPersistModule implements ApplicationListener<ServerStatusEvent> {


    @Override
    public void onApplicationEvent(ServerStatusEvent serverStatusEvent) {

    }
}
