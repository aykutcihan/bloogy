package com.example.bloogy.runner;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class FirestoreAddRunner {

    public static void main(String[] args) {
        try {
            // Firebase Admin SDK başlatma
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/spring-nova.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            Firestore firestore = com.google.firebase.cloud.FirestoreClient.getFirestore();

            // 'articles' koleksiyonuna yeni veri ekle
            CollectionReference articlesRef = firestore.collection("articles");

            // Kitap bilgilerini oluşturma
            Map<String, String> books = new HashMap<>();
            books.put("The Bastard of Istanbul", "A story of family secrets.");
            books.put("The Forty Rules of Love", "A novel about Rumi and love.");
            books.put("Black Milk", "A memoir on motherhood and writing.");
            books.put("Honour", "A story about love and betrayal.");
            books.put("Three Daughters of Eve", "A novel exploring faith and friendship.");


            // Her kitap için döngü
            for (Map.Entry<String, String> book : books.entrySet()) {
                // Kitap bilgilerini doldurma
                Map<String, Object> article = new HashMap<>();
                article.put("title", book.getKey());
                article.put("content", book.getValue());
                article.put("createdDate", Timestamp.now());
                article.put("author", "Elif Shafak");

                // Firestore'a belge ekleme
                ApiFuture<DocumentReference> future = articlesRef.add(article);
                DocumentReference docRef = future.get();

                // Belgeye otomatik oluşturulan ID'yi ekleme
                docRef.update("id", docRef.getId()).get(); // Asenkron tamamlanmasını bekle

                System.out.println("Added book: " + book.getKey() + " with ID: " + docRef.getId());
            }

            System.out.println("Test data added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
