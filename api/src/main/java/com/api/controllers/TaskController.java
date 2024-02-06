package com.api.controllers;

import com.datamodule.dto.TaskCreateDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.NotificationException;
import com.logicmodule.exeptions.TaskException;
import com.logicmodule.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedList;

@CrossOrigin
@RestController
@RequestMapping("api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<TaskDTO> createTask(@ModelAttribute TaskCreateDTO taskDTO)
            throws ModelNotFound, NotificationException, FileException, IOException {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(201).body(createdTask);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<TaskDTO> updateTask(@ModelAttribute TaskCreateDTO taskCreateDTO)
            throws ModelNotFound, TaskException, NotificationException {
        TaskDTO updatedTask = taskService.updateTask(taskCreateDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/{id_task}")
    public ResponseEntity<TaskDTO> getTaskDTO(@PathVariable("id_task") Long id_task)
            throws ModelNotFound {
        TaskDTO taskDTO = taskService.getTaskDTO(id_task);
        return ResponseEntity.ok(taskDTO);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/start/{id_task}/document/{id_document}")
    public ResponseEntity<TaskDTO> startTaskExecute(
            @PathVariable("id_task") Long id_task,
            @PathVariable("id_document") Long id_document) throws ModelNotFound,
            TaskException {
        TaskDTO taskDTO = taskService.startTaskExecute(id_task, id_document);
        return ResponseEntity.ok(taskDTO);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping("/finish/{id_task}/document/{id_document}")
    public ResponseEntity<TaskDTO> finishTaskExecute(
            @PathVariable("id_task") Long id_task, @PathVariable("id_document") Long id_document)
            throws ModelNotFound, TaskException, FileException, EDSException, IOException {
        TaskDTO taskDTO = taskService.finishTaskExecute(id_task, id_document);
        return ResponseEntity.ok(taskDTO);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/document/{id_document}/execute")
    public ResponseEntity<?> getExecuteTaskByDocumentId(
            @PathVariable("id_document") Long id_document,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        var taskDTOs = taskService.getExecuteTaskByDocumentId(id_document, pageable);
        return ResponseEntity.ok(taskDTOs);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/document/{id_document}/wait")
    public ResponseEntity<Page<TaskDTO>> getWaitTasksByDocumentId(
            @PathVariable("id_document") Long id_document,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> tasksPage = taskService.getWaitTasksByDocumentId(id_document, pageable);
        return ResponseEntity.ok(tasksPage);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/document/{id_document}/done")
    public ResponseEntity<Page<TaskDTO>> getDoneTasksByDocumentId(
            @PathVariable("id_document") Long id_document,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> tasksPage = taskService.getDoneTasksByDocumentId(id_document, pageable);
        return ResponseEntity.ok(tasksPage);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/document/{id_document}/notAcceptedDone")
    public ResponseEntity<Page<TaskDTO>> getNotAcceptedDoneTasksByDocumentId(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @PathVariable Long id_document) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> tasksPage = taskService.getAcceptDoneTasks(pageable, id_document);
        return ResponseEntity.ok(tasksPage);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping("/change-priority")
    public ResponseEntity<Page<TaskDTO>> changeOfPriorityInWaitTasks(@RequestBody LinkedList<Long> idTaskUpdate,
                                                                     @RequestParam Long id_document,
                                                                     @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                     @RequestParam(name = "size", required = false, defaultValue = "20") int size) throws ModelNotFound, TaskException {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> updatedTasksPage = taskService
                .changeOfPriorityInWaitTasks(idTaskUpdate, id_document, pageable);
        return ResponseEntity.ok(updatedTasksPage);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping("/rollback/{id_task}/document/{id_document}")
    public ResponseEntity<Void> rollbackToTask(@PathVariable("id_task") Long id_task,
                                               @PathVariable("id_document") Long id_document)
            throws ModelNotFound, TaskException, FileException, IOException {
        taskService.rollbackToTask(id_task, id_document);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/wait-tasks/{id_user}")
    public ResponseEntity<Page<TaskDTO>> getWaitTasksByDocumentIdWithUserId(
            @PathVariable Long id_user,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> tasks = taskService.getWaitTasksByDocumentIdWithUserId(id_user, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/done-tasks/{id_user}")
    public ResponseEntity<Page<TaskDTO>> getDoneTasksByDocumentIdWithUserId(
            @PathVariable Long id_user,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskDTO> tasks = taskService.getDoneTasksByDocumentIdWithUserId(id_user, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id_task}")
    public ResponseEntity<String> deleteTaskById(@PathVariable Long id_task) {
        try {
            taskService.deleteTaskById(id_task);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (ModelNotFound | TaskException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/execute")
    public Page<TaskDTO> getExecuteTasks(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                         @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskService.getExecuteTasks(pageable);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/wait")
    public Page<TaskDTO> getWaitTasks(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskService.getWaitTasks(pageable);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/done")
    public Page<TaskDTO> getDoneTasks(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                      @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskService.getDoneTasks(pageable);
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public Page<TaskDTO> getAllTasks(@RequestParam(name = "page",
            required = false, defaultValue = "0") int page,
                                     @RequestParam(name = "size",
                                             required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskService.getAllTask(pageable);
    }

}
