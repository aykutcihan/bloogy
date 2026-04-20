package com.example.bloogy.unit.repository;

import com.example.bloogy.model.Feedback;
import com.example.bloogy.repository.Impl.FeedbackRepositoryImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackRepositoryImplTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference collectionReference;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> apiFuture;

    @InjectMocks
    private FeedbackRepositoryImpl feedbackRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(firestore.collection("articles")).thenReturn(collectionReference);
    }

    @Test
    void testSaveFeedback() throws ExecutionException, InterruptedException {
        Feedback feedback = new Feedback();
        when(collectionReference.document("123")).thenReturn(documentReference);
        when(documentReference.collection("feedbacks")).thenReturn(collectionReference);
        when(collectionReference.document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("feedbackId");
        when(documentReference.set(feedback)).thenReturn(apiFuture);

        Feedback savedFeedback = feedbackRepository.save("123", feedback);

        assertEquals("feedbackId", savedFeedback.getId());
        verify(documentReference, times(1)).set(feedback);
    }

    @Test
    void testFindByArticleIdAndFeedbackIdNotFound() throws ExecutionException, InterruptedException {
        when(collectionReference.document("123")).thenReturn(documentReference);
        when(documentReference.collection("feedbacks")).thenReturn(collectionReference);
        when(collectionReference.document("1")).thenReturn(documentReference);

        ApiFuture<DocumentSnapshot> mockFuture = mock(ApiFuture.class);
        when(documentReference.get()).thenReturn(mockFuture);
        when(mockFuture.get()).thenReturn(mock(DocumentSnapshot.class));

        Optional<Feedback> result = feedbackRepository.findByArticleIdAndFeedbackId("123", "1");
        assertFalse(result.isPresent());
    }
}
