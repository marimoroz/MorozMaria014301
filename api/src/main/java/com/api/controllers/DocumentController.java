package com.api.controllers;


import com.datamodule.dto.DocumentDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.DocumentException;
import com.logicmodule.service.document.DocumentOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentOperation documentOperation;

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create_document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create_document(@ModelAttribute
                                             DocumentDTO dto)
            throws FileException, DocumentException, IOException, ModelNotFound {
        var doc = documentOperation.saveDocument(dto);
        return ResponseEntity.ok().body(doc);
    }

    @GetMapping(value = "/get_documentBy/{id}")
    public ResponseEntity<?> get_documentById(
            @PathVariable Long id)
            throws FileException, ModelNotFound, IOException {
        return ResponseEntity.ok().body(documentOperation.getDocument(id));
    }

    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/update_document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update_document(@ModelAttribute
                                             DocumentDTO dto)
            throws FileException, DocumentException, ModelNotFound, IOException {
        var doc = documentOperation.updateDocument(dto);
        return ResponseEntity.ok().body(doc);
    }

    @GetMapping(value = "/get_documents")
    public Page<DocumentDTO> get_documents(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size)
            throws FileException, IOException {
        PageRequest pageable = PageRequest.of(page, size);
        return documentOperation.getDocumentByPage(pageable);
    }

    @GetMapping(value = "/getAll_documents")
    public List<DocumentDTO> getAll_documents()
            throws FileException, IOException {
        return documentOperation.getAllDocument();
    }

    @GetMapping(value = "/getAll_documents_archive")
    public List<DocumentDTO> getAll_documents_archive()
            throws FileException, IOException {
        return documentOperation.getDocumentsArchive();
    }

    @DeleteMapping(value = "/deleteDocumentBy/{id}")
    public ResponseEntity<?> deleteDocumentByID(@PathVariable Long id)
            throws FileException, IOException, ModelNotFound {
        documentOperation.deleteDocumentById(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
