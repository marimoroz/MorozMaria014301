package com.logicmodule.service.task.node;

import com.datamodule.dto.DocumentStateDTO;
import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.SequentialTaskNode;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.TaskException;
import jakarta.xml.bind.JAXBException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

public interface NodeService {

    void finishExecuteSequentialTaskNode(Long id_sequential_node_task,
                                         DocumentStateDTO documentStateDTO) throws ModelNotFound, FileException, IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            SignatureException, InvalidKeyException, NoSuchProviderException, JAXBException;

    DocumentStateDTO startExecuteSequentialTaskNode(Long id_sequential_node_task) throws ModelNotFound, FileException, IOException, TaskException, EDSException, NoSuchAlgorithmException, InvalidKeySpecException;

    Page<SequentialTaskNodeDTO> getWaitSequentialTaskNodeByIdTask(Long id_user, Pageable pageable);

    Page<SequentialTaskNodeDTO> getDoneSequentialTaskNodeByIdTask(Long id_user, Pageable pageable);

    Page<SequentialTaskNodeDTO> getExecuteSequentialTaskNode(Long id_user, Pageable pageable) throws ModelNotFound;

    Page<SequentialTaskNodeDTO> changeOfPriorityInWaitTaskNodes(LinkedList<Long> idsSequentialTaskNode, Long id_task, Pageable pageable) throws TaskException;

    void linkedForBdNodes(LinkedList<SequentialTaskNode> created);

}
