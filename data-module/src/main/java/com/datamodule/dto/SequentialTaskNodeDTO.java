package com.datamodule.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SequentialTaskNodeDTO implements Serializable {

    private Long idSequentialTaskNode;

    private Long prevNode;

    private Long nextNode;

    private String name_node;

    private String name_desc;

    private Long count;

    private Boolean isDone;

    private Boolean can_be_done;

    private String directory_path;

    private EmployeeDTO employeeDTO;
}
