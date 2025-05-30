package com.bastionserver.analysis.service;

import com.bastionserver.analysis.service.thread.AlarmThread;
import com.bastionserver.analysis.service.thread.RadarAnalyzerThread;
import com.bastionserver.analysis.service.thread.RadarViewerThread;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    private final RadarAnalyzerThread radarAnalyzerThread;
    private final RadarViewerThread radarViewerThread;
    private final AlarmThread alarmThread;
    private final ExecutorService executorService;
    private final ScheduledExecutorService taskScheduler;

    @Autowired
    public AnalysisService(RadarAnalyzerThread radarAnalyzerThread,
                           RadarViewerThread radarViewerThread,
                           AlarmThread alarmThread,
                           ExecutorService executorService ,
                           ScheduledExecutorService taskScheduler) {
        this.radarAnalyzerThread = radarAnalyzerThread;
        this.radarViewerThread = radarViewerThread;
        this.alarmThread = alarmThread;
        this.executorService = executorService;
        this.taskScheduler = taskScheduler;

        logger.info("Analysis service created");
    }

    @PostConstruct
    public void start() {
        executorService.execute(radarAnalyzerThread);
        executorService.execute(radarViewerThread);
        executorService.execute(alarmThread);
        logger.info("Analysis service started");
    }
}
