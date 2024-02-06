package com.logicmodule.service.task;

import com.datamodule.dto.TaskCreateDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.NotificationException;
import com.logicmodule.exeptions.TaskException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.LinkedList;

public interface TaskService {

    TaskDTO createTask(TaskCreateDTO taskDTO) throws ModelNotFound, NotificationException, FileException, IOException;

    Page<TaskDTO> changeOfPriorityInWaitTasks(LinkedList<Long> idTaskUpdate, Long id_document, Pageable pageable) throws ModelNotFound, TaskException;

    void rollbackToTask(Long id_task, Long id_document) throws ModelNotFound, TaskException, FileException, IOException;

    void deleteTaskById(Long id_task) throws ModelNotFound, TaskException;

    TaskDTO updateTask(TaskCreateDTO taskCreateDTO) throws ModelNotFound, TaskException, NotificationException;

    TaskDTO getTaskDTO(Long id_task) throws ModelNotFound;

    TaskDTO startTaskExecute(Long id_task, Long id_document) throws ModelNotFound, TaskException;

    TaskDTO finishTaskExecute(Long id_task, Long id_document) throws ModelNotFound, TaskException, FileException, IOException, EDSException;

    Page<TaskDTO> getExecuteTaskByDocumentId(Long id_document,Pageable pageable) throws ModelNotFound;

    Page<TaskDTO> getWaitTasksByDocumentId(Long id_document, Pageable pageable) throws ModelNotFound;

    Page<TaskDTO> getDoneTasksByDocumentId(Long id_document, Pageable pageable) throws ModelNotFound;

    Page<TaskDTO> getExecuteTasks(Pageable pageable);

    Page<TaskDTO> getWaitTasks(Pageable pageable);

    Page<TaskDTO> getDoneTasks(Pageable pageable);

    Page<TaskDTO> getWaitTasksByDocumentIdWithUserId(Long id_user, Pageable pageable) throws ModelNotFound;

    Page<TaskDTO> getDoneTasksByDocumentIdWithUserId(Long id_user, Pageable pageable) throws ModelNotFound;

    Page<TaskDTO> getAcceptDoneTasks(Pageable pageable,Long id_document);

    Page<TaskDTO> getAllTask(Pageable pageable);

}
