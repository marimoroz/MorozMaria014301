package com.api.controllers;


import com.logicmodule.service.notification.NotificationOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@CrossOrigin
@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationOperation notificationOperation;

    @DeleteMapping(value = "/delete_notification/{id}")
    public ResponseEntity<?> delete_notification(@PathVariable Long id) {
        notificationOperation.deleteNotificationById(id);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }

    @GetMapping(value = "/get_notification_by_user/{id_user}")
    public ResponseEntity<?> get_notification_by_user(@PathVariable Long id_user) {
        return ResponseEntity.ok(notificationOperation
                .getNotificationByUser(id_user));
    }
}
