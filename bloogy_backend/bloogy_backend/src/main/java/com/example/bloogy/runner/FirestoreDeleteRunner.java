package com.example.bloogy.runner;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

public class FirestoreDeleteRunner {

    public static void main(String[] args) {
        try {
            // Firebase Admin SDK started
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/spring-nova.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            Firestore firestore = com.google.firebase.cloud.FirestoreClient.getFirestore();

            // 'articles' koleksiyonunu seç
            CollectionReference articlesRef = firestore.collection("articles");

            // Koleksiyondaki tüm belgeleri al
            QuerySnapshot querySnapshot = articlesRef.get().get();

            // Belgeleri döngü ile sil
            querySnapshot.getDocuments().forEach(document -> {
                try {
                    document.getReference().delete().get();
                    System.out.println("Silindi: " + document.getId());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("All data deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
