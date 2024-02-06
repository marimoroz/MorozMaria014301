package com.logicmodule.mappers.Impl;

import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.models.SequentialTaskNode;
import com.logicmodule.mappers.EmployeeMapper;
import com.logicmodule.mappers.SequentialTaskNodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component("SequentialTaskMapperImpl")
@RequiredArgsConstructor
public class SequentialTaskMapperImpl implements SequentialTaskNodeMapper {

    private final EmployeeMapper employeeMapper;

    @Override
    public SequentialTaskNodeDTO toDTO(SequentialTaskNode sequentialTaskNode) {
        if (sequentialTaskNode == null) {
            return null;
        }

        SequentialTaskNodeDTO.SequentialTaskNodeDTOBuilder sequentialTaskNodeDTO = SequentialTaskNodeDTO.builder();

        sequentialTaskNodeDTO.idSequentialTaskNode(sequentialTaskNode.getIdSequentialTaskNode());
        sequentialTaskNodeDTO.prevNode(sequentialTaskNode.getPrevNode());
        sequentialTaskNodeDTO.nextNode(sequentialTaskNode.getNextNode());
        sequentialTaskNodeDTO.count(sequentialTaskNode.getCount());
        sequentialTaskNodeDTO.employeeDTO(employeeMapper.toDTO(sequentialTaskNode.getEmployee()));
        sequentialTaskNodeDTO.isDone(sequentialTaskNode.getIsDone());
        sequentialTaskNodeDTO.name_node(sequentialTaskNode.getName_node());
        sequentialTaskNodeDTO.can_be_done(sequentialTaskNode.getCan_be_done());
        sequentialTaskNodeDTO.directory_path(sequentialTaskNode.getDirectory_path());
        sequentialTaskNodeDTO.name_desc(sequentialTaskNode.getName_desc());
        return sequentialTaskNodeDTO.build();
    }

    @Override
    public SequentialTaskNode fromDTO(SequentialTaskNodeDTO sequentialTaskNodeDTO) {
        if (sequentialTaskNodeDTO == null) {
            return null;
        }

        SequentialTaskNode.SequentialTaskNodeBuilder sequentialTaskNode = SequentialTaskNode.builder();

        sequentialTaskNode.idSequentialTaskNode(sequentialTaskNodeDTO.getIdSequentialTaskNode());
        sequentialTaskNode.prevNode(sequentialTaskNodeDTO.getPrevNode());
        sequentialTaskNode.nextNode(sequentialTaskNodeDTO.getNextNode());
        sequentialTaskNode.employee(employeeMapper.fromDTO(sequentialTaskNodeDTO.getEmployeeDTO()));
        sequentialTaskNode.directory_path(sequentialTaskNodeDTO.getDirectory_path());
        sequentialTaskNode.count(sequentialTaskNodeDTO.getCount());
        sequentialTaskNode.name_node(sequentialTaskNodeDTO.getName_node());
        sequentialTaskNode.isDone(sequentialTaskNodeDTO.getIsDone());
        sequentialTaskNode.can_be_done(sequentialTaskNodeDTO.getCan_be_done());
        sequentialTaskNode.name_desc(sequentialTaskNodeDTO.getName_desc());
        return sequentialTaskNode.build();
    }

    @Override
    public LinkedList<SequentialTaskNodeDTO> toLinkedListDTOs(LinkedList<SequentialTaskNode> sequentialTaskNodes) {
        if (sequentialTaskNodes == null) {
            return null;
        }

        LinkedList<SequentialTaskNodeDTO> linkedList = new LinkedList<>();
        for (SequentialTaskNode sequentialTaskNode : sequentialTaskNodes) {
            linkedList.add(toDTO(sequentialTaskNode));
        }

        return linkedList;
    }
}
