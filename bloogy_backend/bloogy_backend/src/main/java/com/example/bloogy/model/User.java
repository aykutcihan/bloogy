package com.example.bloogy.model;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collectionName = "users") // Firestore collection name
public class User {

    @Id // Unique identifier for the user, taken from Google's "sub" value
    private String id;

    private String email; // User's email address
    private String name; // User's full name
    private String role = "ROLE_USER"; // Default role assigned to the user
    private Timestamp createdDate; // Date and time when the user was created
    private Timestamp updatedDate; // Date and time when the user was last updated
}
