package com.example.bloogy.unit.repository;

import com.example.bloogy.model.Article;
import com.example.bloogy.repository.Impl.ArticleRepositoryImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleRepositoryImplTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> apiFuture;

    @InjectMocks
    private ArticleRepositoryImpl articleRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(firestore.collection("articles")).thenReturn(collectionReference);
    }

    @Test
    void testSave() throws ExecutionException, InterruptedException {
        Article article = new Article();
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("docId");
        when(documentReference.set(article)).thenReturn(apiFuture);

        Article saved = articleRepository.save(article);

        assertEquals("docId", saved.getId());
        verify(documentReference, times(1)).set(article);
    }

    @Test
    void testFindById_NotExist() throws ExecutionException, InterruptedException {
        when(collectionReference.document("123")).thenReturn(documentReference);
        DocumentSnapshot mockSnap = mock(DocumentSnapshot.class);
        ApiFuture<DocumentSnapshot> futureSnapshot = mock(ApiFuture.class);
        when(documentReference.get()).thenReturn(futureSnapshot);
        when(futureSnapshot.get()).thenReturn(mockSnap);
        when(mockSnap.exists()).thenReturn(false);

        Article result = articleRepository.findById("123");
        assertNull(result);
    }
}
