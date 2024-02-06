package com.datamodule.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedList;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO implements Serializable {

    private Long idTask;

    private String nameTask;

    private String comment;

    private Instant task_publish;

    private String directory_path;

    private Long count;

    private Long prevTask;

    private Long nextTask;

    private LinkedList<EmployeeDTO> employees;

    private LinkedList<SequentialTaskNodeDTO> sequentialTaskNodeDTOS;

    private EmployeeDTO starter;


}
