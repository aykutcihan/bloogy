package com.example.bloogy.service;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Pub/Sub Subscriber component for handling incoming messages.
 * Automatically starts listening to the specified subscription upon initialization.
 */
@Component
@RequiredArgsConstructor
public class Subscriber {

    private final EmailSender emailSender;

    @Value("${app.features.pubsub-enabled:false}")
    private boolean pubSubEnabled;

    @Value("${app.features.email-enabled:false}")
    private boolean emailEnabled;

    @Value("${app.pubsub.subscription:}")
    private String subscriptionName;

    @Value("${app.notification.email:}")
    private String notificationEmail;

    /**
     * Initializes the subscriber and starts listening to the specified Pub/Sub subscription.
     * This method is automatically invoked when the Spring Bean is created.
     */
    @PostConstruct
    public void startSubscriber() {
        if (!pubSubEnabled || !emailEnabled || subscriptionName == null || subscriptionName.isBlank()) {
            return;
        }

        // Define the message receiver logic
        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            try {
                // Extract the message data
                String postTitle = message.getData().toStringUtf8();

                // Send an email notification
                emailSender.sendEmail(
                        notificationEmail,
                        "New Article Published",    // Email subject
                        "Your new article has been successfully published: " + postTitle // Email body
                );

                // Acknowledge the message as successfully processed
                consumer.ack();
            } catch (Exception e) {
                // If an error occurs, the message will be re-delivered
                consumer.nack();
            }
        };

        // Build and start the Pub/Sub subscriber
        com.google.cloud.pubsub.v1.Subscriber subscriber =
                com.google.cloud.pubsub.v1.Subscriber.newBuilder(subscriptionName, receiver).build();
        subscriber.startAsync().awaitRunning();
    }
}
