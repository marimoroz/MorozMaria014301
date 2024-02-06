package com.logicmodule.service.notification;

import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.dto.TaskDTOMessage;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.User;
import com.datamodule.models.enums.TaskMessage;
import com.logicmodule.exeptions.NotificationException;

import java.util.LinkedList;

public interface NotificationOperation {
    void acceptNotificationFinished(Long idTaskFinished);

    void acceptNotificationSequentialTaskNode(Long idSequentialTaskNodeNew);

    void delegateNotification(User user,
                              TaskMessage taskMessage,
                              Long idTaskFinished,
                              Long idSequentialTaskNodeNew) throws ModelNotFound;


    void notifyEmployeesAboutSequenceNode(LinkedList<SequentialTaskNodeDTO>
                                                  sequentialTaskNodeDTOS,
                                          String command,
                                          LinkedList<Long> id_users,
                                          TaskMessage taskMessage) throws NotificationException;

    LinkedList<TaskDTOMessage> getNotificationByUser(Long id_user);

    void deleteNotificationById(Long id);

}
