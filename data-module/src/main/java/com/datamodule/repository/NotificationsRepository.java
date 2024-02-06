package com.datamodule.repository;

import com.datamodule.models.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository(value = "NotificationsRepository")
public interface NotificationsRepository extends JpaRepository<Notifications,
        Long> {

    @Transactional
    void deleteNotificationsByIdTaskNotification(Long idTaskFinished);

    @Transactional
    void deleteNotificationsByIdSequentialTaskNodeNotification(Long idSequentialTaskNode);

    List<Notifications> findNotificationsByUser_IdUser(Long user_idUser);

    Boolean existsNotificationsByIdSequentialTaskNodeNotification(Long idSequentialTaskNodeNotification);

    Boolean existsNotificationsByIdTaskNotification(Long idTaskNotification);

}
