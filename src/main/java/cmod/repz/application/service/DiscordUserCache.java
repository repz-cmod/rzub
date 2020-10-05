package cmod.repz.application.service;

import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class DiscordUserCache {
    private ConcurrentMap<Object, Object> cache;

    public DiscordUserCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build().asMap();
    }

    public void addToCache(User user){
        cache.putIfAbsent(user.getIdLong(), user);
    }

    public User getUserAndRemove(Long userId){
        Object o = cache.get(userId);
        if(o != null){
            cache.remove(userId);
        }
        return (User) o;
    }

}
