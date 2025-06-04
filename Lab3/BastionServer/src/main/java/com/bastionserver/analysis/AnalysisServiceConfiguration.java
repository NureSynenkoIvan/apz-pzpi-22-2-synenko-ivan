package com.bastionserver.analysis;

import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.devices.DeviceRepository;
import com.bastionserver.analysis.service.strategy.MockStrategy;
import com.bastionserver.analysis.service.strategy.SignalAnalysisStrategy;
import com.bastionserver.analysis.service.thread.AlarmThread;
import com.bastionserver.analysis.service.thread.RadarAnalyzerThread;
import com.bastionserver.analysis.service.thread.RadarViewerThread;
import com.bastionserver.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class AnalysisServiceConfiguration {
    private static final int MAX_THREADS = 10;

    @Bean
    public BlockingQueue<SkyState> skyStatesQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<SkyState> viewsQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<AlarmEvent> alarmsQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public SignalAnalysisStrategy analysisStrategy(DeviceRepository deviceRepository) {
        return new MockStrategy(deviceRepository);
    }

    @Bean
    public RadarAnalyzerThread radarAnalyzerThread(
            BlockingQueue<SkyState> skyStatesQueue,
            BlockingQueue<SkyState> viewsQueue,
            BlockingQueue<SkyState> persistenceQueue,
            BlockingQueue<AlarmEvent> alarmsQueue,
            SignalAnalysisStrategy strategy
    ) {
        return new RadarAnalyzerThread(skyStatesQueue, viewsQueue, persistenceQueue, alarmsQueue, strategy);
    }

    @Bean
    public RadarViewerThread radarViewerThread(BlockingQueue<SkyState> viewsQueue) {
        return new RadarViewerThread(viewsQueue);
    }

    @Bean
    public AlarmThread alarmThread(BlockingQueue<AlarmEvent> alarmsQueue, NotificationService notificationService) {
        return new AlarmThread(alarmsQueue, notificationService);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(MAX_THREADS);
    }
}
