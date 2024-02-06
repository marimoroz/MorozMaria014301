package com.datamodule.models;


import com.datamodule.models.enums.ETypeTaskExecute;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task")
    private Long idTask;

    @Column(name = "name_task")
    private String nameTask;

    @Column(name = "comment")
    private String comment;

    @Column(name = "directory_path")
    private String directory_path;

    @Column(name = "task_publish")
    private Instant task_publish;

    @Column(name = "task_execute")
    @Enumerated(EnumType.STRING)
    private ETypeTaskExecute taskExecute;

    @Column(name = "count")
    private Long count;

    @Column(name = "prev_task")
    private Long prevTask;

    @Column(name = "next_task")
    private Long nextTask;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER,
            cascade =
                    {CascadeType.ALL})
    @OrderBy("count")
    private List<SequentialTaskNode> sequentialTaskNodes;

    @ManyToOne
    @JoinColumn(name = "id_document_wait")
    private Document documentWait;

    @ManyToOne
    @JoinColumn(name = "id_document_done")
    private Document documentDone;

    @OneToOne
    @JoinColumn(name = "id_document_execute")
    private Document documentExecute;

    @OneToOne
    private Document startDocument;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @PrePersist
    public void prePersist() {
        task_publish = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        if (employee != null) {
            employee.getTasks().remove(this);
        }
    }
}