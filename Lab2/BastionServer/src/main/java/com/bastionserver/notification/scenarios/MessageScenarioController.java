package com.bastionserver.notification.scenarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message-scenarios")
public class MessageScenarioController {

    private final MessageScenarioService messageScenarioService;

    @Autowired
    public MessageScenarioController(MessageScenarioService messageScenarioService) {
        this.messageScenarioService = messageScenarioService;
    }

    @GetMapping("/view")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<MessageScenario> getMessageScenario(@RequestParam String scenarioName) {
        MessageScenario scenario = messageScenarioService.findByName(scenarioName).orElse(null);
        if (scenario != null) {
            return ResponseEntity.ok(scenario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<List<MessageScenario>> getAllScenarios() {
        return ResponseEntity.ok(messageScenarioService.findAll());
    }

    @PostMapping
    @Secured("ROLE_administrator")
    public ResponseEntity<Void> addMessageScenario(@RequestBody MessageScenario scenario) {
        messageScenarioService.save(scenario);
        return ResponseEntity.status(201).build(); // CREATED
    }

    @PutMapping
    @Secured("ROLE_administrator")
    public ResponseEntity<Void> updateMessageScenario(@RequestBody MessageScenario scenario) {
        messageScenarioService.update(scenario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Secured("ROLE_administrator")
    public ResponseEntity<Void> deleteMessageScenario(@RequestBody MessageScenario scenario) {
        messageScenarioService.delete(scenario);
        return ResponseEntity.ok().build();
    }
}
