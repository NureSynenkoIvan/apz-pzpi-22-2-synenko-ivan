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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


@Service
public class ArchivingService {
    @Value("${mongo.tools.mongodump-path}")
    private String mongodump;

    @Value("${mongo.tools.mongorestore-path}")
    private String mongorestore;

    @Value("${mongo.tools.periodic-backup-files-output-path}")
    private String periodicBackupOutputPath;

    @Value("${mongo.tools.backup-files-output-path}")
    private String backupOutputPath;

    @Value("${mongo.database}")
    private String databaseName;

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
        this.dailyCleanup = new DailyCleanupTask(skyStateRepository, periodicBackupOutputPath, this);
        scheduler.scheduleAtFixedRate(saving, 10, 10, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(dailyCleanup, 0, 1, TimeUnit.DAYS);
    }


    public void dumpSkyStates(String outputPath) {
        String currentOutputPath = generateTimestampedOutputPath(outputPath);
        List<String> command = Arrays.asList(
                mongodump,
                "--db", databaseName,
                "--collection", "sky_state",
                "--out", currentOutputPath
        );

        executeSubprocess(currentOutputPath, command);
        log.info("Sky States dumped successfully.");
    }

    public void dumpDatabase(String outputPath) {
        String currentOutputPath = generateTimestampedOutputPath(outputPath);

        List<String> command = Arrays.asList(
                mongodump,
                "--db", databaseName,
                "--out", currentOutputPath
        );

        executeSubprocess(currentOutputPath, command);
        log.info("Database dumped successfully.");
    }

    public void restoreFromDate(Date date) {
        String inputPath = backupOutputPath+ "\\"
                + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(date);

        restoreDatabase(inputPath);
    }

    public void restoreDatabase(String inputPath) {

        MongoDatabase database = MongoClients.create("mongodb://localhost:27017/").getDatabase(databaseName);

        database.drop();

        List<String> command = Arrays.asList(
                mongorestore, inputPath
        );

        executeSubprocess(inputPath, command);
    }

    public List<Date> listBackupVersions() {
        File outputDir = new File(backupOutputPath);
        File[] directories = outputDir.listFiles(File::isDirectory);

        if (directories == null) return List.of();

        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

        return Arrays.stream(directories)
                .map(File::getName)
                .map(name -> {
                    try {
                        return format.parse(name);
                    } catch (ParseException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }

    private String generateTimestampedOutputPath(String outputPath) {
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
        return currentOutputPath;
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
