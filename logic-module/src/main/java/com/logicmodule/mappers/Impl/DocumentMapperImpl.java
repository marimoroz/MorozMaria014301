package com.logicmodule.mappers.Impl;

import com.datamodule.dto.DocumentDTO;
import com.datamodule.models.Document;
import com.logicmodule.mappers.DocumentMapper;
import com.logicmodule.mappers.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("DocumentMapperImpl")
@RequiredArgsConstructor
public class DocumentMapperImpl implements DocumentMapper {

    private final EmployeeMapper employeeMapper;

    @Override
    public DocumentDTO toDTO(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentDTO.DocumentDTOBuilder documentDTO = DocumentDTO.builder();

        documentDTO.employeeDTO( employeeMapper.toDTO( document.getEmployee() ) );
        documentDTO.idDocument( document.getIdDocument() );
        documentDTO.fileName( document.getFileName() );
        documentDTO.fileDirectory( document.getFileDirectory() );
        documentDTO.document_publish( document.getDocument_publish() );

        return documentDTO.build();
    }

    @Override
    public DocumentDTO toDTOWithOut(Document document) {
        if ( document == null ) {
            return null;
        }

        DocumentDTO.DocumentDTOBuilder documentDTO = DocumentDTO.builder();

        documentDTO.idDocument( document.getIdDocument() );
        documentDTO.fileName( document.getFileName() );
        documentDTO.fileDirectory( document.getFileDirectory() );
        documentDTO.document_publish( document.getDocument_publish() );

        return documentDTO.build();
    }

    @Override
    public Document fromDTO(DocumentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Document.DocumentBuilder document = Document.builder();

        document.employee( employeeMapper.fromDTO( dto.getEmployeeDTO() ) );
        document.idDocument( dto.getIdDocument() );
        document.fileName( dto.getFileName() );
        document.fileDirectory( dto.getFileDirectory() );
        document.document_publish( dto.getDocument_publish() );

        return document.build();
    }

    @Override
    public Document fromDTOWithOut(DocumentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Document.DocumentBuilder document = Document.builder();

        document.idDocument( dto.getIdDocument() );
        document.fileName( dto.getFileName() );
        document.fileDirectory( dto.getFileDirectory() );
        document.document_publish( dto.getDocument_publish() );

        return document.build();
    }

    @Override
    public void update(Document document, DocumentDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getFileName() != null ) {
            document.setFileName( dto.getFileName() );
        }
        if ( dto.getFileDirectory() != null ) {
            document.setFileDirectory( dto.getFileDirectory() );
        }
        if ( dto.getDocument_publish() != null ) {
            document.setDocument_publish( dto.getDocument_publish() );
        }
        if ( dto.getEDocumentType() != null ) {
            document.setEDocumentType( dto.getEDocumentType() );
        }
    }
}
