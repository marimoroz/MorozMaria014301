package com.datamodule.models;

import com.datamodule.models.enums.EDocumentType;
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
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_directory")
    private String fileDirectory;

    @Column(name = "document_publish")
    private Instant document_publish;

    @OneToOne(mappedBy = "documentExecute",fetch = FetchType.EAGER, cascade =
            {CascadeType.ALL})
    private Task executeTask;

    @Column(name = "is_archive")
    private Boolean isArchive;

    @Column(name = "document_type")
    @Enumerated(EnumType.STRING)
    private EDocumentType eDocumentType;

    @OneToMany(mappedBy = "documentWait", fetch = FetchType.LAZY, cascade =
            {CascadeType.ALL})
    @OrderBy("count")
    private List<Task> waitTasks;

    @OneToMany(mappedBy = "documentDone", fetch = FetchType.LAZY, cascade =
            {CascadeType.ALL})
    @OrderBy("count")
    private List<Task> doneTask;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @PrePersist
    public void prePersist() {
        document_publish = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        document_publish = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        if (employee != null) {
            employee.getDocuments().remove(this);
        }
    }
}