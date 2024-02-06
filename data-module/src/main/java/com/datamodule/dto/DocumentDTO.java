package com.datamodule.dto;

import com.datamodule.models.enums.EDocumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO implements Serializable {

    private Long idDocument;

    private String fileName;

    private String fileDirectory;

    private Instant document_publish;

    private EDocumentType eDocumentType;

    private Long id_employee_publisher;

    @JsonIgnore
    private transient MultipartFile file;

    private byte[] file_in_byte;

    private EmployeeDTO employeeDTO;
}
