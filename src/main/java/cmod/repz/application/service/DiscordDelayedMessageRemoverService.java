package cmod.repz.application.service;

import net.dv8tion.jda.api.entities.Message;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class DiscordDelayedMessageRemoverService {
    private final TaskScheduler messageRemoveTaskScheduler;

    public DiscordDelayedMessageRemoverService(TaskScheduler messageRemoveTaskScheduler) {
        this.messageRemoveTaskScheduler = messageRemoveTaskScheduler;
    }

    public void scheduleRemove(Message message, int seconds){
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, seconds);
        messageRemoveTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                message.delete().queue();
            }
        }, calendar.getTime());
    }
}
