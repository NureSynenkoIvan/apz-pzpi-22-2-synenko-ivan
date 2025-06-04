package com.bastionserver.archiving;


import jakarta.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/archive")
public class ArchivingController {

    @Value("${mongo.tools.backup-files-output-path}")
    private String path;

    private ArchivingService archivingService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    public ArchivingController(ArchivingService archivingService) {
        this.archivingService = archivingService;
    }

    @PostMapping
    @Secured({"ROLE_administrator"})
    public ResponseEntity dumpDatabase() {
        archivingService.dumpDatabase(path);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Secured({"ROLE_administrator"})
    public ResponseEntity<List<Date>> listBackupVersions() {
        return ResponseEntity.ok().body(archivingService.listBackupVersions());
    }


    @PostMapping
    @CrossOrigin
    @RequestMapping("/restore")
    @Secured({"ROLE_administrator"})
    public ResponseEntity restoreDatabase(@RequestParam("date") String dateString) throws ParseException {
        Date date = format.parse(dateString);
        //OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateString);
        //Date date = Date.from(offsetDateTime.toInstant());

        archivingService.restoreFromDate(date);
        return ResponseEntity.ok().build();
    }

}
