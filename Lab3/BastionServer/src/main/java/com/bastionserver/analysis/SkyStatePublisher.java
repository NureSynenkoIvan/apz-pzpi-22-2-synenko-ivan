package com.bastionserver.analysis;


import com.bastionserver.analysis.model.SkyState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class SkyStatePublisher {

    private final BlockingQueue<SkyState> skyStatesQueue;

    public SkyStatePublisher(
            @Qualifier("skyStatesQueue") BlockingQueue<SkyState> skyStatesQueue
    ) {
        this.skyStatesQueue = skyStatesQueue;
    }

    public void publish(SkyState skyState) {
        try {
            skyStatesQueue.put(skyState);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while publishing SkyState", e);
        }
    }
}
