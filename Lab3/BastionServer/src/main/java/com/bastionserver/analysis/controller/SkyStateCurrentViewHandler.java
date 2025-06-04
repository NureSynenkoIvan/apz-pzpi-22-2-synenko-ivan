package com.bastionserver.analysis.controller;

import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.analysis.service.thread.RadarViewerThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;

@Component
@CrossOrigin
public class SkyStateCurrentViewHandler extends TextWebSocketHandler {
    private RadarViewerThread radarViewerThread;
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    public SkyStateCurrentViewHandler(
                                      RadarViewerThread radarViewerThread) {
        this.radarViewerThread = radarViewerThread;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage(
                ow.writeValueAsString(
                        radarViewerThread.getCurrentSkyState())));
    }
}
