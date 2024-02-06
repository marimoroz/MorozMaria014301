package com.datamodule.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employee")
    private Long idEmployee;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "patronymic")
    private String patronymic;

    @OneToMany(mappedBy = "employee",fetch = FetchType.LAZY)
    private Set<SequentialTaskNode> sequentialTaskNodes;

    @OneToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "employee" ,fetch = FetchType.LAZY)
    private Set<Document> documents;

    @OneToMany(mappedBy = "employee" ,fetch = FetchType.LAZY)
    private Set<Task> tasks;

}
