package com.logicmodule.mappers.Impl;

import com.datamodule.dto.TaskCreateDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.models.Task;
import com.logicmodule.mappers.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("TaskMapperImpl")
@RequiredArgsConstructor
public class TaskMapperImpl implements TaskMapper {
    @Override
    public TaskCreateDTO toTaskCreateDTO(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskCreateDTO.TaskCreateDTOBuilder taskCreateDTO = TaskCreateDTO.builder();

        taskCreateDTO.idTask( task.getIdTask() );
        taskCreateDTO.nameTask( task.getNameTask() );
        taskCreateDTO.comment( task.getComment() );
        taskCreateDTO.task_publish( task.getTask_publish() );
        taskCreateDTO.directory_path( task.getDirectory_path() );

        return taskCreateDTO.build();
    }

    @Override
    public Task fromDTO(TaskCreateDTO taskCreateDTO) {
        if ( taskCreateDTO == null ) {
            return null;
        }

        Task.TaskBuilder task = Task.builder();

        task.idTask( taskCreateDTO.getIdTask() );
        task.nameTask( taskCreateDTO.getNameTask() );
        task.comment( taskCreateDTO.getComment() );
        task.directory_path( taskCreateDTO.getDirectory_path() );
        task.task_publish( taskCreateDTO.getTask_publish() );

        return task.build();
    }

    @Override
    public TaskDTO toDTO(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDTO.TaskDTOBuilder taskDTO = TaskDTO.builder();

        taskDTO.idTask( task.getIdTask() );
        taskDTO.nameTask( task.getNameTask() );
        taskDTO.comment( task.getComment() );
        taskDTO.task_publish( task.getTask_publish() );
        taskDTO.directory_path( task.getDirectory_path() );
        taskDTO.count( task.getCount() );
        taskDTO.prevTask( task.getPrevTask() );
        taskDTO.nextTask( task.getNextTask() );

        return taskDTO.build();
    }

    @Override
    public void update(Task task, TaskCreateDTO taskDTO) {
        if ( taskDTO == null ) {
            return;
        }

        if ( taskDTO.getIdTask() != null ) {
            task.setIdTask( taskDTO.getIdTask() );
        }
        if ( taskDTO.getNameTask() != null ) {
            task.setNameTask( taskDTO.getNameTask() );
        }
        if ( taskDTO.getComment() != null ) {
            task.setComment( taskDTO.getComment() );
        }
        if ( taskDTO.getDirectory_path() != null ) {
            task.setDirectory_path( taskDTO.getDirectory_path() );
        }
        if ( taskDTO.getTask_publish() != null ) {
            task.setTask_publish( taskDTO.getTask_publish() );
        }
    }
}
