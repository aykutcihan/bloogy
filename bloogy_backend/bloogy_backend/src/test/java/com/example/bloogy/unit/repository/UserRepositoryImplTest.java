package com.example.bloogy.unit.repository;

import com.example.bloogy.model.User;
import com.example.bloogy.repository.Impl.UserRepositoryImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {

    @Mock
    private Firestore firestore;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultApiFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotApiFuture;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(firestore.collection("users")).thenReturn(mock(CollectionReference.class));
    }

    @Test
    void testSaveUser() throws ExecutionException, InterruptedException {
        User user = User.builder().id("123").email("test@example.com").name("Test User").build();
        when(firestore.collection("users").document(user.getId())).thenReturn(documentReference);
        when(documentReference.set(user)).thenReturn(writeResultApiFuture);

        User result = userRepository.save(user);

        assertEquals("123", result.getId());
        verify(documentReference, times(1)).set(user);
    }

    @Test
    void testFindById_UserExists() throws ExecutionException, InterruptedException {
        String userId = "123";
        User user = User.builder().id(userId).email("test@example.com").name("Test User").build();

        when(firestore.collection("users").document(userId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotApiFuture);
        when(documentSnapshotApiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(User.class)).thenReturn(user);

        Optional<User> result = userRepository.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(documentReference, times(1)).get();
    }

    @Test
    void testFindById_UserDoesNotExist() throws ExecutionException, InterruptedException {
        String userId = "123";
        when(firestore.collection("users").document(userId)).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotApiFuture);
        when(documentSnapshotApiFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        Optional<User> result = userRepository.findById(userId);

        assertFalse(result.isPresent());
        verify(documentReference, times(1)).get();
    }
}
