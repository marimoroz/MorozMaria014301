package com.api.controllers;

import com.datamodule.dto.DocumentStateDTO;
import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.TaskException;
import com.logicmodule.service.task.node.NodeService;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;

@CrossOrigin
@RestController
@RequestMapping("api/nodes")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    @GetMapping("/start/{id_sequential_node_task}")
    public ResponseEntity<DocumentStateDTO> startExecuteSequentialTaskNode(
            @PathVariable("id_sequential_node_task") Long id_sequential_node_task)
            throws ModelNotFound, FileException, IOException, TaskException, EDSException, NoSuchAlgorithmException, InvalidKeySpecException {
        DocumentStateDTO documentStateDTO = nodeService
                .startExecuteSequentialTaskNode(id_sequential_node_task);
        return ResponseEntity.ok(documentStateDTO);
    }

    @GetMapping("/wait-task-nodes/{id_user}")
    public ResponseEntity<Page<SequentialTaskNodeDTO>> getWaitSequentialTaskNodeByIdTask
            (@PathVariable("id_user") Long id_user,
             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
             @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SequentialTaskNodeDTO> taskNodesPage = nodeService
                .getWaitSequentialTaskNodeByIdTask(id_user, pageable);
        return ResponseEntity.ok(taskNodesPage);
    }

    @GetMapping("/done-task-nodes/{id_user}")
    public ResponseEntity<Page<SequentialTaskNodeDTO>> getDoneSequentialTaskNodeByIdTask(
            @PathVariable("id_user") Long id_user,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SequentialTaskNodeDTO> taskNodesPage = nodeService
                .getDoneSequentialTaskNodeByIdTask(id_user, pageable);
        return ResponseEntity.ok(taskNodesPage);
    }

    @GetMapping("/execute/{id_user}")
    public ResponseEntity<?> getExecuteSequentialTaskNode(
            @PathVariable("id_user") Long id_user,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) throws ModelNotFound {
        Pageable pageable = PageRequest.of(page, size);
        var taskNodeDTOs = nodeService.
                getExecuteSequentialTaskNode(id_user, pageable);
        return ResponseEntity.ok(taskNodeDTOs);
    }

    @PutMapping("/change-priority")
    public ResponseEntity<Page<SequentialTaskNodeDTO>> changeOfPriorityInWaitTaskNodes(
            @RequestBody LinkedList<Long> idsSequentialTaskNode,
            @RequestParam Long id_task,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size)
            throws TaskException {
        Pageable pageable = PageRequest.of(page, size);
        Page<SequentialTaskNodeDTO> updatedTaskNodesPage = nodeService
                .changeOfPriorityInWaitTaskNodes(idsSequentialTaskNode, id_task, pageable);
        return ResponseEntity.ok(updatedTaskNodesPage);
    }

    @PostMapping(value = "/finish_node/{id_node_next}")
    public void finishExecuteSequentialTaskNode(@ModelAttribute DocumentStateDTO documentStateDTO,
                                                @PathVariable Long id_node_next)
            throws ModelNotFound, FileException, IOException,
            NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException,
            NoSuchProviderException, JAXBException {
        nodeService.finishExecuteSequentialTaskNode(id_node_next, documentStateDTO);
    }

}
