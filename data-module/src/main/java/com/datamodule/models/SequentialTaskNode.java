package com.datamodule.models;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sequential_task_nodes")
public class SequentialTaskNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sequential_task_node")
    private Long idSequentialTaskNode;

    @Column(name = "prev_node")
    private Long prevNode;

    @Column(name = "next_node")
    private Long nextNode;

    @Column(name = "name_node")
    private String name_node;

    @Column(name = "name_desc")
    private String name_desc;

    @Column(name = "directory_path")
    private String directory_path;

    @Column(name = "count")
    private Long count;

    @OneToOne(mappedBy = "sequentialTaskNode", cascade = CascadeType.ALL)
    private DocumentState documentState;

    @ManyToOne
    @JoinColumn(name = "id_task")
    private Task task;

    @Column(name = "is_done")
    private Boolean isDone;

    @Column(name = "сan_be_done")
    private Boolean can_be_done;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;


    @PreRemove
    public void preRemove() {
        if (employee != null) {
            employee.getSequentialTaskNodes().remove(this);
        }

        employee = null;
    }

}
