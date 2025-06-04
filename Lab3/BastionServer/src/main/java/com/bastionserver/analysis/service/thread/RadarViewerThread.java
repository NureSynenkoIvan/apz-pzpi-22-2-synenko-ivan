package com.bastionserver.analysis.service.thread;

import com.bastionserver.analysis.model.SkyState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class RadarViewerThread extends Thread {
    Logger logger = LoggerFactory.getLogger(RadarViewerThread.class);
    private final BlockingQueue<SkyState> viewsQueue;
    ObjectMapper mapper = new ObjectMapper();

    private SkyState currentSkyState;

    public RadarViewerThread(BlockingQueue<SkyState> viewsQueue) {
        this.viewsQueue = viewsQueue;
    }

    public SkyState getCurrentSkyState() {
        return currentSkyState;
    }

    @Override
    public void run() {
        //This thread is needed for demonstrating the state of sky.
        //It will be used by the web app to create real-time view.

        logger.info("RadarViewerThread started");
        while (true) {
            try {
                currentSkyState = viewsQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.info("RadarViewerThread received skyState");

            //This represents radar receiving.
            try {
                System.out.println("Showing skyState:" + mapper.writeValueAsString(currentSkyState));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}