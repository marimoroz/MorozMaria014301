package com.logicmodule.mappers;

import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.models.SequentialTaskNode;

import java.util.LinkedList;

public interface SequentialTaskNodeMapper {

    SequentialTaskNodeDTO toDTO(SequentialTaskNode sequentialTaskNode);

    SequentialTaskNode fromDTO(SequentialTaskNodeDTO sequentialTaskNodeDTO);

    LinkedList<SequentialTaskNodeDTO> toLinkedListDTOs(LinkedList<SequentialTaskNode> sequentialTaskNodes);
}
