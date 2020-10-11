package cmod.repz.application.config;

public class DiscordStateHolder {
    private static volatile boolean ready;

    public static void setReady(boolean ready) {
        DiscordStateHolder.ready = ready;
    }

    public static boolean isReady() {
        return ready;
    }
}
