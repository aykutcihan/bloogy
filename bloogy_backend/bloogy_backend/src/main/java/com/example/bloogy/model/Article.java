package com.example.bloogy.model;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Article {

    @Id // Firestore automatically generates an ID for the document
    private String id;

    private String title;
    private String content;
    private String author;

    private Timestamp createdDate;
    private Timestamp updatedDate;
}
