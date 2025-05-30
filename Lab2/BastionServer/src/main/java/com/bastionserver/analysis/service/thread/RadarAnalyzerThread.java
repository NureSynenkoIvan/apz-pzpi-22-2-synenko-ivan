package com.bastionserver.analysis.service.thread;

import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.analysis.service.strategy.SignalAnalysisStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RadarAnalyzerThread extends Thread {
    Logger logger = LoggerFactory.getLogger(RadarAnalyzerThread.class);

    private final BlockingQueue<SkyState> skyStatesQueue;
    private final BlockingQueue<SkyState> viewsQueue;
    private final BlockingQueue<SkyState> savingQueue;
    private final BlockingQueue<AlarmEvent> alarmsQueue;
    private final SignalAnalysisStrategy strategy;

    private SkyState currentSkyState;

    public RadarAnalyzerThread(BlockingQueue<SkyState> skyStatesQueue,
                               BlockingQueue<SkyState> viewsQueue,
                               BlockingQueue<SkyState> savingQueue,
                               BlockingQueue<AlarmEvent> alarmsQueue,
                               SignalAnalysisStrategy strategy) {
        this.skyStatesQueue = skyStatesQueue;
        this.viewsQueue = viewsQueue;
        this.savingQueue = savingQueue;
        this.alarmsQueue = alarmsQueue;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        logger.info("RadarAnalyzerThread started");
        try {
            while (true) {
                currentSkyState = skyStatesQueue.take();
                logger.info("RadarAnalyzerThread received skyState");

                //Saving data to DB and viewing is delegated to different threads.
                viewsQueue.put(currentSkyState);
                savingQueue.put(currentSkyState);

                //Analysis is delegated to strategy, so we can change it without changing this file.
                //Get list of threatening objects.
                List<SkyObject> threateningObjects = strategy.analyze(currentSkyState);
                logger.info("RadarAnalyzerThread analyzed skyState");

                if (! threateningObjects.isEmpty()) {
                    AlarmEvent alarmEvent = new AlarmEvent(currentSkyState, threateningObjects);
                    alarmsQueue.put(alarmEvent);
                }


            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}