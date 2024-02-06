package com.datamodule.models;

import com.datamodule.models.enums.EDocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_states")
public class DocumentState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocumentState;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_directory")
    private String fileDirectory;

    @Column(name = "comment")
    private String comment;

    @Column(name = "document_publish")
    private Instant document_publish;

    @Column(name = "document_type")
    @Enumerated(EnumType.STRING)
    private EDocumentType eDocumentType;

    @OneToOne
    @JoinColumn(name = "id_sequential_task_node")
    private SequentialTaskNode sequentialTaskNode;

    @PrePersist
    public void prePersist() {
        document_publish = Instant.now();
    }
}
