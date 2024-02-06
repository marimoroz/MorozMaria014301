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
public class DocumentStateDTO implements Serializable {

    private Long idDocumentState;

    private String fileName;

    private String fileDirectory;

    private String comment;

    private Instant document_publish;

    private EDocumentType eDocumentType;

    private byte[] file_in_byte;



    @JsonIgnore
    private transient MultipartFile file;

}
