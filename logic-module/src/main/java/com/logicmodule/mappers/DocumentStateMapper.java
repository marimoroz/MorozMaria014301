package com.logicmodule.mappers;


import com.datamodule.dto.DocumentStateDTO;
import com.datamodule.models.DocumentState;


public interface DocumentStateMapper {

    DocumentStateDTO toDTO(DocumentState documentState);

    DocumentState fromDTO(DocumentStateDTO documentStateDTO);

}
