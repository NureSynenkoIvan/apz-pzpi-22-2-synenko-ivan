package com.bastionserver.notification.notifiers.impl.firebase;

import com.bastionserver.employees.model.Employee;
import com.bastionserver.notification.NotificationMessage;
import com.bastionserver.notification.notifiers.Notifier;
import com.bastionserver.notification.scenarios.MessageScenario;
import com.bastionserver.notification.scenarios.MessageScenarioRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import static com.bastionserver.notification.notifiers.impl.firebase.FirebaseConfiguration.sendMessage;

@Component
public class FirebaseAndroidMobileNotifier implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(FirebaseAndroidMobileNotifier.class);
    private ConcurrentHashMap<Employee, String> activeFCMTokens;

    private MessageScenario currentAlarm = new MessageScenario("Air Alert",
            "Увага! Оголошена повітряна тривога! Негайно прослідуйте до укриття",
            "Відбій повітряної тривоги");

    @Autowired
    public FirebaseAndroidMobileNotifier(ConcurrentHashMap<Employee, String> activeFCMTokens, MessageScenarioRepository messageScenarioRepository) {
        try {
            this.activeFCMTokens = activeFCMTokens;
            initializeFirebase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeFirebase() throws Exception {
        Resource serviceAccount = new ClassPathResource("credentials.json");

        InputStream inputStream = serviceAccount.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();

        FirebaseApp.initializeApp(options);
        log.info("FirebaseApp initialized successfully.");
    }


    @Override
    public void sendAlarmNotification(NotificationMessage event) {
        for (Employee employee : activeFCMTokens.keySet()) {
            try {
                sendMessage(activeFCMTokens.get(employee), true, currentAlarm.getOnAlertStart());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }

    @Override
    public void sendStopAlarmNotification() {

        for (Employee employee : activeFCMTokens.keySet()) {
            try {
                sendMessage(activeFCMTokens.get(employee), false, currentAlarm.getOnAlertFinish());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }



}
