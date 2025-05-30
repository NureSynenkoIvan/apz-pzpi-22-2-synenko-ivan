package com.bastionserver.notification.scenarios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageScenarioService {
    private MessageScenarioRepository messageScenarioRepository;

    @Autowired
    public MessageScenarioService(MessageScenarioRepository messageScenarioRepository) {
        this.messageScenarioRepository = messageScenarioRepository;
    }

    public MessageScenario save(MessageScenario messageScenario) {
        return messageScenarioRepository.save(messageScenario);
    }

    public List<MessageScenario> findAll() {
        return messageScenarioRepository.findAll();
    }

    public MessageScenario update(MessageScenario messageScenario) {
        return messageScenarioRepository.save(messageScenario);
    }

    public void delete(MessageScenario messageScenario) {
        messageScenarioRepository.delete(messageScenario);
    }

    public Optional<MessageScenario> findByName(String name) {
        return messageScenarioRepository.findByScenarioName(name);
    }
}
