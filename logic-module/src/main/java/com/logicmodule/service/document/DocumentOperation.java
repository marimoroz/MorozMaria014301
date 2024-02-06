package com.logicmodule.service.document;

import com.datamodule.dto.DocumentDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Document;
import com.datamodule.models.DocumentState;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.DocumentException;
import org.apache.commons.fileupload.FileItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface DocumentOperation {
    DocumentDTO saveDocument(DocumentDTO dto) throws FileException, DocumentException, IOException, ModelNotFound;

    DocumentDTO getDocument(Long id_document) throws FileException, ModelNotFound, IOException;

    DocumentDTO updateDocument(DocumentDTO dto) throws FileException, IOException, ModelNotFound, DocumentException;

    Page<DocumentDTO> getDocumentByPage(Pageable pageable) throws FileException, IOException;

    List<DocumentDTO> getAllDocument() throws FileException, IOException;

    Document findDocumentByIDWithTasks(Long id_document) throws ModelNotFound;

    Document findDocumentByID(Long id_document) throws ModelNotFound;

    Document findDocumentByIdWithTasksWait(Long id_document) throws ModelNotFound;

    Document findDocumentByIdWithTasksDone(Long id_document) throws ModelNotFound;

    Document createCopyDocumentToArchive(Document document, FileItem fileItem) throws FileException;

    List<DocumentDTO> getDocumentsArchive() throws FileException, IOException;

    void deleteDocumentById(Long id) throws ModelNotFound, FileException, IOException;

}
