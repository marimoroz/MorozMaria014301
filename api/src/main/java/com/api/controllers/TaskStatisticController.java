package com.api.controllers;

import com.datamodule.exeptions.ModelNotFound;
import com.logicmodule.service.task.TaskStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@CrossOrigin
@RestController
@RequestMapping("api/tasks/statistic")
@RequiredArgsConstructor
public class TaskStatisticController {

    private final TaskStatistic taskStatistic;

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/percent-execute/{id_task}")
    public ResponseEntity<Double> getPercentExecute(@PathVariable Long id_task) {
        try {
            double percent = taskStatistic.percentExecute(id_task);
            return ResponseEntity.ok(percent);
        } catch (ModelNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/statistics/{id_document}")
    public ResponseEntity<?> getAllStatisticByTask(@PathVariable Long id_document) {
        try {
            var statistics = taskStatistic.getAllStatisticByTask(id_document);
            return ResponseEntity.ok(statistics);
        } catch (ModelNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/percent-rollback/{id_document}")
    public ResponseEntity<Double> getPercentRollBack(@PathVariable Long id_document) {
        try {
            double percent = taskStatistic.percentRollBack(id_document);
            return ResponseEntity.ok(percent);
        } catch (ModelNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
