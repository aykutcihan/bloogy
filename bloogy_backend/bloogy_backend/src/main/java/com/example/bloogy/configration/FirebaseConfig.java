package com.example.bloogy.configration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for Firebase Firestore integration.
 * This class initializes the Firestore instance for the application.
 */
@Configuration
public class FirebaseConfig {

    @Value("${app.gcp.credentials.path}")
    private String credentialsPath;

    @Value("${app.gcp.project-id:}")
    private String projectId;

    /**
     * Configures and provides a Firestore instance.
     * Reads Firebase service account credentials from an external file path.
     *
     * @return Firestore instance for database operations.
     * @throws IOException if the credentials file cannot be read.
     */
    @Bean
    public Firestore firestore() throws IOException {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            throw new IllegalStateException("Missing GCP credentials path. Set GOOGLE_APPLICATION_CREDENTIALS or app.gcp.credentials.path.");
        }

        InputStream serviceAccount = new FileInputStream(credentialsPath);

        // Configure Firebase options with the credentials
        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount));

        if (projectId != null && !projectId.isBlank()) {
            optionsBuilder.setProjectId(projectId);
        }

        FirebaseOptions options = optionsBuilder.build();

        // Initialize FirebaseApp if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        // Return Firestore client instance
        return FirestoreClient.getFirestore();
    }
}
