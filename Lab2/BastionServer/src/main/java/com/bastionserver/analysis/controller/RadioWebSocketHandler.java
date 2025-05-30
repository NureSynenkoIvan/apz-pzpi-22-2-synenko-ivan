package com.bastionserver.analysis.controller;

import com.bastionserver.analysis.model.DroneIdData;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.service.RadioAggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RadioWebSocketHandler extends TextWebSocketHandler {

    private final RadioAggregationService aggregator;
    private final ObjectMapper mapper = new ObjectMapper();

    public RadioWebSocketHandler(RadioAggregationService aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        IncomingMessageWrapper wrapper = mapper.readValue(message.getPayload(), IncomingMessageWrapper.class);

        switch (wrapper.getType()) {
            case "drone" -> {
                DroneIdData[] droneData = mapper.treeToValue(wrapper.getPayload(), DroneIdData[].class);
                List<SkyObject> skyObjects = Arrays.stream(droneData)
                        .map(SkyObject::new)
                        .toList();
                aggregator.onDroneData(skyObjects);
            }
            case "signal" -> {
                Signal[] signals = mapper.treeToValue(wrapper.getPayload(), Signal[].class);
                aggregator.onSignalData(Arrays.asList(signals));
            }
            default -> {
                session.sendMessage(new TextMessage("ERROR: Unknown type"));
                return;
            }
        }

        session.sendMessage(new TextMessage("ACK"));
    }
}