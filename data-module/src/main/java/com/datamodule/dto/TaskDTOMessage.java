package com.datamodule.dto;


import com.datamodule.models.enums.TaskMessage;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TaskDTOMessage implements Serializable {

    private Long idNotification;

    private TaskMessage taskMessage;

    private Long idTaskFinished;

    private Long idSequentialTaskNodeNew;
}
