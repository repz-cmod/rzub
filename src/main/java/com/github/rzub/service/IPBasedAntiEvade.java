package com.github.rzub.service;

public interface IPBasedAntiEvade {
    boolean shouldBlock(String clientId, String ip);
    boolean shouldBlock(String ip);
}
