package com.datamodule.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDTO implements Serializable {

    private Long idTask;

    private String nameTask;

    private String comment;

    private LinkedList<String> node_name;

    private LinkedList<String> name_desc;

    private Instant task_publish;

    private String directory_path;

    private LinkedList<Long> id_employee;

    private Long id_employee_creator;

    private Long id_document;

}
