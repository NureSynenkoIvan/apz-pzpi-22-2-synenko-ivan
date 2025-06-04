package com.bastionserver.archiving;

import com.bastionserver.analysis.model.SkyState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ArchivingConfiguration {
    @Bean
    public BlockingQueue<SkyState> persistenceQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ScheduledExecutorService taskScheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
