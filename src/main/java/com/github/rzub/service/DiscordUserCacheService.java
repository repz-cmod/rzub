package com.github.rzub.service;

import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class DiscordUserCacheService {
    private ConcurrentMap<Object, Object> cache;

    public DiscordUserCacheService() {
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
