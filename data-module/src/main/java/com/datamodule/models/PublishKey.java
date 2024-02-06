package com.datamodule.models;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publishKeys")
public class PublishKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPublishKey;

    @Column(name = "publish_key")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] publishKey;

    @OneToOne
    @JoinColumn(name = "id_user")
    private User user;

}
