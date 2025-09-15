package com.example.gymcrm.api;

import com.example.gymcrm.dao.TrainingTypeDao;
import com.example.gymcrm.dto.TrainingTypeResponse;
import com.example.gymcrm.entity.TrainingType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Training Types")
@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeController(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @ApiOperation("Get training types")
    @GetMapping
    public List<TrainingTypeResponse> list() {
        return trainingTypeDao.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TrainingTypeResponse toDto(TrainingType tt) {
        TrainingTypeResponse r = new TrainingTypeResponse();
        r.setId(tt.getId());
        r.setName(tt.getName());
        return r;
    }
}
