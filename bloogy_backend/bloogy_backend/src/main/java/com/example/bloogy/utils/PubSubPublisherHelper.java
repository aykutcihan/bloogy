package com.example.bloogy.utils;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for publishing messages to Google Pub/Sub topics.
 * Encapsulates the use of the PubSubTemplate for message publishing.
 */
@Component
public class PubSubPublisherHelper {

    private final PubSubTemplate pubSubTemplate;

    /**
     * Constructor to initialize the PubSubTemplate.
     *
     * @param pubSubTemplate the PubSubTemplate used for interacting with Google Pub/Sub.
     */
    public PubSubPublisherHelper(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    /**
     * Publishes a message to a specified Pub/Sub topic.
     *
     * @param topicName the name of the topic to publish to.
     * @param message the message to be published.
     */
    public void publishMessage(String topicName, String message) {
        pubSubTemplate.publish(topicName, message);
    }
}
