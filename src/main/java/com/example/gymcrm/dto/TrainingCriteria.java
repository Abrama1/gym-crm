package com.example.gymcrm.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class TrainingCriteria {
    private LocalDateTime from;           // inclusive, nullable
    private LocalDateTime to;             // exclusive, nullable
    private String trainingType;          // exact name or null
    private String otherPartyNameLike;    // e.g., "%john doe%" or null (for trainee view = trainer name, for trainer view = trainee name)
}
