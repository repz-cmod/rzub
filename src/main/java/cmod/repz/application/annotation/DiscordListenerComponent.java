package cmod.repz.application.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordListenerComponent {
    String command();
}
