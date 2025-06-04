package com.bastionserver.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@CrossOrigin
public class WebSocketConfiguration implements WebSocketConfigurer {
    private RadioWebSocketHandler radioWebSocketHandler;
    private SkyStateCurrentViewHandler skyStateCurrentViewHandler;

    @Autowired
    public WebSocketConfiguration(RadioWebSocketHandler radioWebSocketHandler, SkyStateCurrentViewHandler skyStateCurrentViewHandler) {
        this.radioWebSocketHandler = radioWebSocketHandler;
        this.skyStateCurrentViewHandler = skyStateCurrentViewHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /*registry
                .addHandler(radioWebSocketHandler, "/ws-radar")
                .addHandler(skyStateCurrentViewHandler, "/ws-skystate")
                .setAllowedOrigins("*");
    */}
}