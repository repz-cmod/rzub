package com.github.rzub.filter;

import com.github.rzub.model.SettingsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class PluginTokenFilter extends OncePerRequestFilter {
    private final String TOKEN_HEAD_NAME = "Token";
    private final SettingsModel settingsModel;

    public PluginTokenFilter(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = httpServletRequest.getHeader(TOKEN_HEAD_NAME);
        if (token == null){
            log.warn("Unauthorized request from " + httpServletRequest.getRemoteAddr() + " to plugins address.");
            return;
        }

        if (token.equals(settingsModel.getSecurity().getToken())) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }else {
            log.warn("Unauthorized request from " + httpServletRequest.getRemoteAddr() + " with token `" + token + "` to plugins address.");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return !path.startsWith("/plugin/");
    }
}
