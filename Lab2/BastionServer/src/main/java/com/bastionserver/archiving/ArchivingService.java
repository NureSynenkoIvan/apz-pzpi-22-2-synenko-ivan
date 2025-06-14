package com.bastionserver.archiving;

import com.bastionserver.analysis.service.database.SkyStateRepository;
import com.bastionserver.analysis.model.SkyState;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ArchivingService {
    @Value("${mongo.tools.mongodump-path}")
    private String mongodump;

    @Value("${mongo.tools.mongorestore-path}")
    private String mongorestore;

    @Value("${mongo.tools.backup-files-output-path}")
    private String backupOutputPath;

    private String databaseName = "bastion";

    private Logger log = LoggerFactory.getLogger(ArchivingService.class);
    private SaveTask saving;
    private DailyCleanupTask dailyCleanup;
    private final SkyStateRepository skyStateRepository;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public ArchivingService(@Qualifier("persistenceQueue") BlockingQueue<SkyState> persistenceQueue,
                            SkyStateRepository skyStateRepository,
                            ScheduledExecutorService scheduler) {
        this.skyStateRepository = skyStateRepository;
        this.saving = new SaveTask(persistenceQueue, skyStateRepository);
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void start() {
        this.dailyCleanup = new DailyCleanupTask(skyStateRepository, backupOutputPath, this);
        scheduler.scheduleAtFixedRate(saving, 10, 10, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(dailyCleanup, 0, 1, TimeUnit.DAYS);
    }


    public void dumpSkyStates(String outputPath) {
        // Generate a unique folder name based on the current date and time
        String timestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
        String currentOutputPath = outputPath + "\\" + timestamp;

        // Create the directory if it doesn't exist
        try {
            Path outputDir = Paths.get(currentOutputPath);
            Files.createDirectories(outputDir);
            log.info("Created output directory: " + currentOutputPath);
        } catch (IOException e) {
            log.error("Failed to create output directory: " + currentOutputPath, e);
            throw new RuntimeException("Could not create output directory", e);
        }
        List<String> command = Arrays.asList(
                mongodump,
                "--db", databaseName,
                "--collection", "sky_state",
                "--out", currentOutputPath
        );

        executeSubprocess(currentOutputPath, command);
    }

    public void dumpDatabase(String outputPath) {
        List<String> command = Arrays.asList(
                mongodump,
                "--db", databaseName,
                "--out", outputPath
        );

        executeSubprocess(outputPath, command);
    }

    public void restoreDatabase(String outputPath) {
        List<String> command = Arrays.asList(
                mongorestore,
                "--db", "bastion",
                "--out", outputPath
        );

        executeSubprocess(outputPath, command);
    }

    private void executeSubprocess(String path, List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command).directory(new File(path));
        log.info("Executing command: " + pb.command());
        int exitCode = 0;
        try {
            Process process = pb.start();
            exitCode = process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        log.info("exitCode:" + exitCode);
    }

    private static class DailyCleanupTask implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(DailyCleanupTask.class);

        private final String backupOutputPath;

        private final SkyStateRepository skyStateRepository;
        private final ArchivingService archivingService;

        public DailyCleanupTask(SkyStateRepository skyStateRepository, String backupOutputPath, ArchivingService archivingService) {
            this.skyStateRepository = skyStateRepository;
            this.archivingService = archivingService;
            this.backupOutputPath = backupOutputPath;
        }

        @Override
        public void run() {
            try {
                logger.info("Performing daily cleanup...");

                archivingService.dumpSkyStates(backupOutputPath);
                logger.info("Saved dump of all sky states of this day");

                logger.info("Cleaning all sky states of this day");
                skyStateRepository.deleteAll();
                logger.info("Cleanup complete.");

                archivingService.dumpDatabase(backupOutputPath);
                logger.info("Database dumped successfully.");


            } catch (Exception e) {
                logger.error("Error during daily cleanup: " + e.getMessage());
            }
        }
    }

    private static class SaveTask implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(SaveTask.class);
        private static final int MAX_BUFFER_SIZE = 3; //For test reasons, can be any reasonable number

        private final BlockingQueue<SkyState> saveQueue;
        private final SkyStateRepository skyStateRepository;

        public SaveTask(BlockingQueue<SkyState> saveQueue,
                        SkyStateRepository skyStateRepository) {
            this.saveQueue = saveQueue;
            this.skyStateRepository = skyStateRepository;
        }

        @Override
        public void run() {
            //It better be called periodically to avoid wasting system resources.
            try {
                if (saveQueue.size() > MAX_BUFFER_SIZE) {
                    List<SkyState> buffer = new ArrayList<>();
                    saveQueue.drainTo(buffer);
                    skyStateRepository.saveAll(buffer);
                    logger.info("Saved {} states to the database.", buffer.size());
                } else {
                    logger.debug("Current buffer size is {}, thread goes back to sleep", saveQueue.size());
                }
            } catch (Exception e) {
                logger.error("Error during save operation", e);
            }
        }
    }

}
