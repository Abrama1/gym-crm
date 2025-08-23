package com.example.gymcrm.service;

import com.example.gymcrm.dao.TrainingDao;
import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.domain.Training;
import com.example.gymcrm.domain.TrainingType;
import com.example.gymcrm.exceptions.NotFoundException;
import com.example.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock TrainingDao trainingDao;
    @Mock TrainingTypeDao trainingTypeDao;

    @InjectMocks TrainingServiceImpl service;

    @BeforeEach
    void setup(){ MockitoAnnotations.openMocks(this); }

    @Test
    void create_withExistingType_succeeds() {
        Training tr = new Training();
        tr.setTrainingName("Morning Run");
        tr.setTrainingType("Cardio");
        tr.setTrainingDate(LocalDateTime.now());
        tr.setDurationMinutes(30);

        when(trainingTypeDao.findByName("Cardio"))
                .thenReturn(Optional.of(new TrainingType()));
        when(trainingDao.save(any())).thenAnswer(inv -> {
            Training x = inv.getArgument(0);
            x.setId(10L);
            return x;
        });

        Training saved = service.create(tr);
        assertEquals(10L, saved.getId());
        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void create_withMissingType_throws() {
        Training tr = new Training();
        tr.setTrainingName("Yoga Flow");
        tr.setTrainingType("Yoga");

        when(trainingTypeDao.findByName("Yoga"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(tr));
        verify(trainingDao, never()).save(any());
    }

    @Test
    void getById_and_list_delegateToDao() {
        Training t = new Training(); t.setId(5L);
        when(trainingDao.findById(5L)).thenReturn(Optional.of(t));
        assertTrue(service.getById(5L).isPresent());

        when(trainingDao.findAll()).thenReturn(java.util.List.of(t));
        assertEquals(1, service.list().size());
    }
}
