package com.logicmodule.mappers;

import com.datamodule.dto.DocumentDTO;
import com.datamodule.models.Document;


public interface DocumentMapper {

    DocumentDTO toDTO(Document document);

    DocumentDTO toDTOWithOut(Document document);

    Document fromDTO(DocumentDTO dto);

    Document fromDTOWithOut(DocumentDTO dto);

    void update(Document document, DocumentDTO dto);

}
