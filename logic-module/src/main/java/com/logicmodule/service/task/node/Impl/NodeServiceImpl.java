package com.logicmodule.service.task.node.Impl;

import com.datamodule.dto.DocumentStateDTO;
import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.SequentialTaskNode;
import com.datamodule.models.enums.ETypeTaskExecute;
import com.datamodule.models.enums.TaskMessage;
import com.datamodule.repository.DocumentStateRepository;
import com.datamodule.repository.SequentialTaskNodeRepository;
import com.file.exceptions.FileException;
import com.file.service.FileCRUD;
import com.file.service.Impl.DocFileMapper;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.TaskException;
import com.logicmodule.mappers.DocumentStateMapper;
import com.logicmodule.mappers.SequentialTaskNodeMapper;
import com.logicmodule.service.eds.EdsService;
import com.logicmodule.service.notification.NotificationOperation;
import com.logicmodule.service.task.node.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Slf4j
@Service(value = "NodeServiceImpl")
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final SequentialTaskNodeRepository sequentialTaskNodeRepository;

    private final FileCRUD fileCRUD;

    private final DocumentStateRepository documentStateRepository;

    private final DocumentStateMapper documentStateMapper;

    private final SequentialTaskNodeMapper sequentialTaskNodeMapper;

    private final NotificationOperation notificationOperation;

    @Override
    @Transactional
    public void finishExecuteSequentialTaskNode(Long id_sequential_node_task,
                                                DocumentStateDTO documentStateDTO)
            throws ModelNotFound, FileException, IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, SignatureException, InvalidKeyException, NoSuchProviderException {
        var sequential_node = findByID(id_sequential_node_task);
        var privateKey = sequential_node.getEmployee().getUser().getPrivateKey();
        var username = sequential_node.getEmployee().getUser().getUsername();
        // add document signature
        var documentDocx = DocFileMapper.convertHtmlToDocx(documentStateDTO.getFile().getBytes());

        var signature = EdsService.signDocument(new ByteArrayInputStream(documentDocx),
                EdsService.bytesToPrivateKey(privateKey));
        var doc_with_signature = EdsService.combineDocumentAndSignature(
                documentDocx, signature, username);
        fileCRUD.saveFile(doc_with_signature,
                documentStateDTO.getFileDirectory(), documentStateDTO.getFileName(),
                documentStateDTO.getFile().getContentType());
        var document_state = documentStateMapper.fromDTO(documentStateDTO);
        sequential_node.setIsDone(true);
        var sec = sequentialTaskNodeRepository.save(sequential_node);
        document_state.setSequentialTaskNode(sec);
        documentStateRepository.save(document_state);


        if (sequential_node.getNextNode() != -1L && sequential_node.getNextNode() > 0) {

            var sequential_node_next = findByID(sequential_node.getNextNode());
            sequential_node_next.setCan_be_done(true);
            sequentialTaskNodeRepository.save(sequential_node_next);


            notificationOperation.delegateNotification(sequential_node_next
                            .getEmployee()
                            .getUser(), TaskMessage.DO_TASK, -1L,
                    sequential_node_next.getIdSequentialTaskNode());


        } else if (sequential_node.getNextNode() == -1L) {


            // update document with last document state
            var document = sequential_node.getTask().getDocumentExecute();

            fileCRUD.saveFile(
                    doc_with_signature, document.getFileName()
                    , document.getFileDirectory(), "docx");

            sequential_node.getTask().setTaskExecute(ETypeTaskExecute.DONE);
            sequentialTaskNodeRepository.save(sequential_node);
            // create notification about finish all task
        }
    }

    private SequentialTaskNode findByID(Long id_node) throws ModelNotFound {
        return sequentialTaskNodeRepository.findById(id_node)
                .orElseThrow(() -> new ModelNotFound("sequential_node doesn't " +
                        "exist with id_node: "
                        + id_node));
    }

    private LinkedList<SequentialTaskNode> findExecuteSequentialTaskNodesByTaskId(Long id_user) {
        return sequentialTaskNodeRepository.findSequentialTaskNodesByUserId(id_user,false,true);
    }

    @Override
    public DocumentStateDTO startExecuteSequentialTaskNode(Long id_sequential_node_task)
            throws ModelNotFound, FileException, IOException, TaskException, EDSException, NoSuchAlgorithmException, InvalidKeySpecException {
        var sequential_node = findByID(id_sequential_node_task);
        String path = sequential_node.getTask().getDirectory_path() + "\\"
                + sequential_node.getCount() + sequential_node.getEmployee().getSurname();
        if (sequential_node.getCan_be_done()) {
            if (sequential_node.getPrevNode() != -1L) {
                var documentState = findByID(sequential_node.getPrevNode())
                        .getDocumentState();
                var doc = fileCRUD.getFile(documentState.getFileName(),
                        documentState.getFileDirectory());
                var sequential_node_prev = findByID(sequential_node.getPrevNode());
                var user = sequential_node_prev.getEmployee().getUser();
                var publicKey = sequential_node_prev.getEmployee().getUser()
                        .getPublishKey().getPublishKey();
                var pair = EdsService
                        .splitDocumentAndSignature(doc.get(), user
                                .getUsername());

                    return DocumentStateDTO
                            .builder()
                            .document_publish(Instant.now())
                            .comment("")
                            .fileName(documentState.getFileName())
                            .fileDirectory(path)
                            .file_in_byte(DocFileMapper.convertDocxToHtml(doc.get()
                            ))
                            .build();

            } else {
                var document = sequential_node.getTask().getDocumentExecute();
                return DocumentStateDTO
                        .builder()
                        .document_publish(Instant.now())
                        .comment("")
                        .fileDirectory(path)
                        .fileName(document.getFileName())
                        .file_in_byte(DocFileMapper.convertDocxToHtml(fileCRUD.getFile(document.getFileName(),
                                document.getFileDirectory()).get()))
                        .build();
            }
        } else {
            throw new TaskException("prev task or task node are not finished yet");
        }
    }

    @Override
    @Transactional
    public Page<SequentialTaskNodeDTO> getWaitSequentialTaskNodeByIdTask(Long id_user,
                                                                         Pageable pageable) {
        var waitNodes = sequentialTaskNodeRepository
                .findSequentialTaskNodesByUserId(id_user,false,false);
        log.info(waitNodes.size()+"wait");
        return getSequentialTaskNodeDTOSFromPage(pageable, waitNodes);
    }

    @Override
    @Transactional
    public Page<SequentialTaskNodeDTO> getDoneSequentialTaskNodeByIdTask(Long id_user,
                                                                         Pageable pageable) {
        var waitNodes = sequentialTaskNodeRepository
                .findSequentialTaskNodesByUserId(id_user, true, true);
        log.info(waitNodes.size()+"done");
        return getSequentialTaskNodeDTOSFromPage(pageable, waitNodes);
    }

    @NotNull
    private Page<SequentialTaskNodeDTO> getSequentialTaskNodeDTOSFromPage(Pageable pageable, LinkedList<SequentialTaskNode> waitNodes) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, waitNodes.size());
        var waitNodesDTOs = waitNodes.subList(start, end).stream()
                .map(sequentialTaskNodeMapper::toDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return new PageImpl<>(waitNodesDTOs, pageable, waitNodes.size());
    }

    @Override
    @Transactional
    public Page<SequentialTaskNodeDTO> getExecuteSequentialTaskNode(Long id_user, Pageable pageable) {
        var sequential = findExecuteSequentialTaskNodesByTaskId(id_user);
        log.info(sequential.size() + "exe");
        return getSequentialTaskNodeDTOSFromPage(pageable, sequential);
    }

    @Override
    public Page<SequentialTaskNodeDTO> changeOfPriorityInWaitTaskNodes(LinkedList<Long> idsSequentialTaskNode,
                                                                       Long id_task, Pageable pageable)
            throws TaskException {
        var waitNodes = sequentialTaskNodeRepository
                .findDoneSequentialTaskNodesByTaskId(id_task);
        var executeNode = sequentialTaskNodeRepository
                .findExecuteSequentialTaskNodesByTaskId(id_task).orElse(null);
        var doneNodes = sequentialTaskNodeRepository
                .findDoneSequentialTaskNodesByTaskId(id_task);
        var updated = new LinkedList<>(waitNodes);
        if (waitNodes.size() != updated.size()) {
            throw new TaskException("change of priority not done");
        }
        for (SequentialTaskNode taskNode : waitNodes) {
            if (!idsSequentialTaskNode.contains(taskNode.getIdSequentialTaskNode())) {
                throw new TaskException("change of priority not done");
            }
            int index = idsSequentialTaskNode.indexOf(taskNode.getIdSequentialTaskNode());
            updated.set(index, taskNode);
        }
        linkedForBdNodes(updated);
        if (executeNode != null) {
            executeNode.setNextNode(updated.getFirst().getIdSequentialTaskNode());
            updated.getFirst().setPrevNode(executeNode.getIdSequentialTaskNode());
            sequentialTaskNodeRepository.save(executeNode);
        }
        if (!doneNodes.isEmpty() && executeNode == null) {
            doneNodes.getLast().setNextNode(updated.getFirst().getIdSequentialTaskNode());
            updated.getFirst().setPrevNode(doneNodes.getLast().getIdSequentialTaskNode());
            sequentialTaskNodeRepository.saveAll(doneNodes);
        }
        sequentialTaskNodeRepository.saveAll(updated);
        return getSequentialTaskNodeDTOSFromPage(pageable, waitNodes);
    }

    @Override
    public void linkedForBdNodes(LinkedList<SequentialTaskNode> created) {
        Long prevNode = -1L;
        for (int i = 0; i < created.size(); i++) {
            Long nextNode = (i < created.size() - 1) ? created.get(i + 1).getIdSequentialTaskNode() : -1L;
            var node = created.get(i);
            node.setNextNode(nextNode);
            node.setPrevNode(prevNode);
            prevNode = node.getIdSequentialTaskNode();
        }
    }
}
