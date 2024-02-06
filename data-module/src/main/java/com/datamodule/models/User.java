package com.datamodule.models;

import com.datamodule.models.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Employee employee;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<Notifications> notifications;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "private_key")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] privateKey;

    @OneToOne(mappedBy = "user")
    private PublishKey publishKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "e_role", nullable = false)
    private ERole eRole;
}
