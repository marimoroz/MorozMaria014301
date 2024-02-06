package com.logicmodule.service.notification.Impl;


import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.dto.TaskDTOMessage;
import com.datamodule.models.Notifications;
import com.datamodule.models.User;
import com.datamodule.models.enums.ERole;
import com.datamodule.models.enums.TaskMessage;
import com.datamodule.repository.NotificationsRepository;
import com.logicmodule.exeptions.NotificationException;
import com.logicmodule.service.notification.NotificationOperation;
import com.logicmodule.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service(value = "NotificationOperationImpl")
@RequiredArgsConstructor
public class NotificationOperationImpl implements NotificationOperation {

    private final NotificationsRepository notificationsRepository;


    private final UserService userService;


    @Override
    public void acceptNotificationFinished(Long idTaskFinished) {
        if (notificationsRepository.existsNotificationsByIdTaskNotification(idTaskFinished)) {
            notificationsRepository.deleteNotificationsByIdTaskNotification(idTaskFinished);
        }
    }

    @Override
    public void acceptNotificationSequentialTaskNode(Long idSequentialTaskNodeNew) {
        if (notificationsRepository.existsNotificationsByIdSequentialTaskNodeNotification(idSequentialTaskNodeNew)) {
            notificationsRepository.deleteNotificationsByIdSequentialTaskNodeNotification(idSequentialTaskNodeNew);
        }
    }

    @Override
    public void delegateNotification(User user, TaskMessage taskMessage,
                                     Long idTaskFinished,
                                     Long idSequentialTaskNodeNew) {
        notificationsRepository
                .save(Notifications
                        .builder()
                        .content(taskMessage)
                        .isActive(true)
                        .user(user)
                        .idTaskNotification(idTaskFinished)
                        .idSequentialTaskNodeNotification(idSequentialTaskNodeNew)
                        .build());
    }



    @Override
    public void notifyEmployeesAboutSequenceNode(LinkedList<SequentialTaskNodeDTO> sequentialTaskNodeDTOS,
                                                 String command,
                                                 LinkedList<Long> id_users, TaskMessage taskMessage) throws NotificationException {
        if (sequentialTaskNodeDTOS.size() != id_users.size()) {
            throw new NotificationException("notification exception");
        }
        AtomicInteger i = new AtomicInteger();
        userService.findByIdUserInAndRoleName(id_users, ERole.ROLE_USER)
                .forEach(user -> {
                    notificationsRepository.save(
                            Notifications
                                    .builder()
                                    .isActive(true)
                                    .content(taskMessage)
                                    .user(user)
                                    .idSequentialTaskNodeNotification(sequentialTaskNodeDTOS
                                            .get(i.get()).getIdSequentialTaskNode())
                                    .build()
                    );
                    i.getAndIncrement();
                });

    }


    @Override
    public LinkedList<TaskDTOMessage> getNotificationByUser(Long id_user) {
        return notificationsRepository.findNotificationsByUser_IdUser(id_user)
                .stream().map(
                        notification ->
                                TaskDTOMessage
                                        .builder()
                                        .idNotification(notification.getIdNotifications())
                                        .idSequentialTaskNodeNew(notification.getIdSequentialTaskNodeNotification())
                                        .idTaskFinished(notification.getIdTaskNotification())
                                        .taskMessage(notification.getContent())
                                        .build()
                ).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional
    public void deleteNotificationById(Long id) {
        if (notificationsRepository.existsById(id)) {
            notificationsRepository.deleteById(id);
        }
    }

}
