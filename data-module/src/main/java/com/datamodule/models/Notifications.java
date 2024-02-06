package com.datamodule.models;


import com.datamodule.models.enums.TaskMessage;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_websocket_user_session")
    private Long idNotifications;

    @Column(name = "content", nullable = false)
    @Enumerated
    private TaskMessage content;

    @Column(name = "id_task_notification")
    private Long idTaskNotification;

    @Column(name = "id_sequential_task_node_notification")
    private Long idSequentialTaskNodeNotification;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @PreRemove
    public void preRemove()
    {
        user.getNotifications().remove(this);
    }

}
