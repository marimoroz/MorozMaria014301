package com.logicmodule.mappers;

import com.datamodule.dto.TaskCreateDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.models.Task;


public interface TaskMapper {
    TaskCreateDTO toTaskCreateDTO(Task task);
    Task fromDTO(TaskCreateDTO taskCreateDTO);
    TaskDTO toDTO(Task task);
    void update(Task task,TaskCreateDTO taskDTO);
}
