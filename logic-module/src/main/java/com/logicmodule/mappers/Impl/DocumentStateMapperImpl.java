package com.logicmodule.mappers.Impl;

import com.datamodule.dto.DocumentStateDTO;
import com.datamodule.models.DocumentState;
import com.logicmodule.mappers.DocumentStateMapper;
import com.logicmodule.mappers.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("DocumentStateMapperImpl")
@RequiredArgsConstructor
public class DocumentStateMapperImpl implements DocumentStateMapper {

    private final EmployeeMapper employeeMapper;

    @Override
    public DocumentStateDTO toDTO(DocumentState documentState) {
        if (documentState == null) {
            return null;
        }

        DocumentStateDTO.DocumentStateDTOBuilder documentStateDTO = DocumentStateDTO.builder();

        documentStateDTO.idDocumentState(documentState.getIdDocumentState());
        documentStateDTO.fileName(documentState.getFileName());
        documentStateDTO.fileDirectory(documentState.getFileDirectory());
        documentStateDTO.comment(documentState.getComment());
        documentStateDTO.document_publish(documentState.getDocument_publish());

        return documentStateDTO.build();
    }

    @Override
    public DocumentState fromDTO(DocumentStateDTO documentStateDTO) {
        if (documentStateDTO == null) {
            return null;
        }

        DocumentState.DocumentStateBuilder documentState = DocumentState.builder();

        documentState.idDocumentState(documentStateDTO.getIdDocumentState());
        documentState.fileName(documentStateDTO.getFileName());
        documentState.fileDirectory(documentStateDTO.getFileDirectory());
        documentState.comment(documentStateDTO.getComment());

        return documentState.build();
    }
}
