package com.bastionserver.notification.notifiers.impl.firebase;

import com.bastionserver.employees.model.Employee;
import com.google.firebase.database.utilities.Pair;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class FirebaseConfiguration {
    @Bean
    public ConcurrentHashMap<Employee, String> activeFCMTokens() {
        return new ConcurrentHashMap<Employee, String>();
    }

    @Bean
    public BlockingQueue<Employee> locatedEmployees() {
        return new LinkedBlockingQueue<Employee>();
    }

    public static void sendMessage(String fcmToken, boolean isAlarm, String message) throws FirebaseMessagingException {
        Message messageToSend = Message.builder()

                .putData("isAlarm", String.valueOf(isAlarm))
                .putData("message", message)
                .setToken(fcmToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(messageToSend);
        System.out.println("Successfully sent message: " + response);
    }

}
