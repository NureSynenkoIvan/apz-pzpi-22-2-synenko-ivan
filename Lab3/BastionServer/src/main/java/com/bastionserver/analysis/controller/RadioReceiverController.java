package com.bastionserver.analysis.controller;

import com.bastionserver.analysis.model.DroneIdData;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.service.RadioAggregationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/radio")
public class RadioReceiverController {
    private RadioAggregationService aggregator;
    private final ObjectMapper mapper = new ObjectMapper();


    @Autowired
    public RadioReceiverController(RadioAggregationService aggregator) {
        this.aggregator = aggregator;
    }

    @PostMapping
    public void receive(@RequestBody IncomingMessageWrapper wrapper) throws Exception {

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
                throw new Exception("Unknown message type");
            }
        }
    }
}
