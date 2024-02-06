package com.logicmodule.service.document.Impl;

import com.datamodule.dto.DocumentDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Document;
import com.datamodule.models.SequentialTaskNode;
import com.datamodule.models.Task;
import com.datamodule.repository.DocumentRepository;
import com.file.exceptions.FileException;
import com.file.service.FileCRUD;
import com.logicmodule.exeptions.DocumentException;
import com.logicmodule.mappers.DocumentMapper;
import com.logicmodule.service.document.DocumentOperation;
import com.logicmodule.service.notification.NotificationOperation;
import com.logicmodule.service.user.EmployService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service(value = "DocumentOperationImpl")
@RequiredArgsConstructor
public class DocumentOperationImpl implements DocumentOperation {

    private final DocumentRepository documentRepository;

    private final FileCRUD fileCRUD;

    private final DocumentMapper documentMapper;

    private final EmployService employService;

    @Value("${document.directory}")
    private String document_directory;

    @Value("${document.archive}")
    private String document_archive_directory;

    private final NotificationOperation notificationOperation;

    @Override
    @Transactional
    public DocumentDTO saveDocument(DocumentDTO dto) throws FileException,
            DocumentException, IOException, ModelNotFound {
        if (dto == null || dto.getFile().isEmpty()) {
            throw new DocumentException("file is null");
        }
        var employ = employService.getEmployeeModelById(dto.getId_employee_publisher());
        var document = documentMapper.fromDTOWithOut(dto);
        document.setFileDirectory(document_directory);
        document.setEmployee(employ);
        document.setIsArchive(false);
        document = documentRepository.save(document);
        fileCRUD.saveFile(dto.getFile().getBytes(), document_directory, dto.getFileName()
                , "docx");
        dto.setIdDocument(document.getIdDocument());
        return dto;
    }

    @Override
    public DocumentDTO getDocument(Long id_document) throws FileException,
            ModelNotFound, IOException {
        log.info("");
        var doc = this.findDocumentByID(id_document);
        var docDTO = documentMapper.toDTO(doc);
        var byte_file = fileCRUD.getFile(doc.getFileName(),
                doc.getFileDirectory()).get();
        docDTO.setFile_in_byte(byte_file);
        return docDTO;
    }


    @Override
    public Document findDocumentByIDWithTasks(Long id_document) throws ModelNotFound {
        var docDone = this.findDocumentByIdWithTasksDone(id_document);
        var docWait = this.findDocumentByIdWithTasksWait(id_document);
        docDone.setWaitTasks(docWait.getWaitTasks());
        return docDone;
    }

    @Override
    public Document findDocumentByID(Long id_document) throws ModelNotFound {
        return documentRepository.findById(id_document)
                .orElseThrow(() -> new ModelNotFound("document with id: " + id_document
                        + " doesn't exist"));
    }

    @Override
    public Document findDocumentByIdWithTasksWait(Long id_document) throws ModelNotFound {
        return documentRepository.findDocumentByIdDocumentWithTasksWait(id_document)
                .orElseThrow(() -> new ModelNotFound("document with id: " + id_document
                        + " doesn't exist"));
    }

    @Override
    public Document findDocumentByIdWithTasksDone(Long id_document) throws ModelNotFound {
        return documentRepository.findDocumentByIdDocumentWithTasksDone(id_document)
                .orElseThrow(() -> new ModelNotFound("document with id: " + id_document
                        + " doesn't exist"));
    }

    @Override
    public Document createCopyDocumentToArchive(Document document, FileItem fileItem)
            throws FileException {
        var doc = Document.builder()
                .fileDirectory(document_archive_directory)
                .fileName(document.getFileName())
                .eDocumentType(document.getEDocumentType())
                .document_publish(Instant.now())
                .isArchive(true)
                .build();
        doc = documentRepository.save(doc);
        fileCRUD.saveFile(fileItem.get(), document_archive_directory, document.getFileName()
                , "docx");
        return doc;
    }

    public DocumentDTO updateDocument(DocumentDTO dto) throws FileException,
            IOException, ModelNotFound, DocumentException {
        if (dto == null || dto.getFile().isEmpty()) {
            throw new DocumentException("file is null");
        }
        var doc = this.findDocumentByID(dto.getIdDocument());
        fileCRUD.deleteFile(doc.getFileDirectory(), doc.getFileName());
        documentMapper.update(doc, dto);
        documentRepository.save(doc);
        fileCRUD.saveFile(dto.getFile().getBytes(), doc.getFileDirectory(),
                dto.getFileName(), "docx");
        return dto;
    }

    @Override
    public Page<DocumentDTO> getDocumentByPage(Pageable pageable)
            throws FileException, IOException {
        var documents = documentRepository.findAll(pageable)
                .get().toList();
        return new PageImpl<>(convertWithFile(documents));
    }

    @Override
    @Transactional
    public List<DocumentDTO> getAllDocument()
            throws FileException, IOException {
        var documents = documentRepository.findDocumentsByIsArchive(false);
        return convertWithFile(documents);
    }



    private List<DocumentDTO> convertWithFile(List<Document> documents)
            throws FileException, IOException {
        var docDTOs = new ArrayList<DocumentDTO>();
        for (Document document : documents) {
            var docDTO = documentMapper.toDTO(document);
            var byte_file = fileCRUD
                    .getFile(docDTO.getFileName(), docDTO
                            .getFileDirectory()).get();
            docDTO.setFile_in_byte(byte_file);
            docDTOs.add(docDTO);
        }
        return docDTOs;
    }

    @Override
    @Transactional
    public List<DocumentDTO> getDocumentsArchive()
            throws FileException, IOException {
        var documents = documentRepository.findDocumentsByIsArchive(true);
        return convertWithFile(documents);
    }

    @Override
    @Transactional
    public void deleteDocumentById(Long id)
            throws ModelNotFound, FileException, IOException {
        var doc = findDocumentByIdWithTasksWait(id);
        var docDone = findDocumentByIdWithTasksDone(id);
        fileCRUD.deleteFile(doc.getFileName(), doc.getFileDirectory());
        var tasks = doc.getWaitTasks();
        if (docDone.getDoneTask() != null && !docDone.getDoneTask().isEmpty()) {
            tasks.addAll(docDone.getDoneTask());
        }

        if (doc.getExecuteTask() != null) {
            tasks.add(doc.getExecuteTask());
        }
        var listNodeTasks = new ArrayList<SequentialTaskNode>();
        for (Task t : tasks) {
            listNodeTasks.addAll(t.getSequentialTaskNodes());
        }
        var listNodeIdsTasks = listNodeTasks.stream().map(
                SequentialTaskNode::getIdSequentialTaskNode
        ).collect(Collectors.toCollection(ArrayList::new));
        var listIdsTask = tasks.stream().map(Task::getIdTask)
                .collect(Collectors.toCollection(ArrayList::new));
        listNodeIdsTasks.forEach(
                notificationOperation::acceptNotificationSequentialTaskNode);
        listIdsTask.forEach(
                notificationOperation::acceptNotificationFinished
        );
        documentRepository.deleteById(id);
    }
}
