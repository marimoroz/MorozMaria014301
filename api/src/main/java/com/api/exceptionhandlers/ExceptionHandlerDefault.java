package com.api.exceptionhandlers;

import com.datamodule.exeptions.ModelNotFound;
import com.file.exceptions.FileException;
import com.logicmodule.exeptions.ArchiveException;
import com.logicmodule.exeptions.DocumentException;
import com.logicmodule.exeptions.NotificationException;
import com.logicmodule.exeptions.TaskException;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice("ExceptionHandler")
public class ExceptionHandlerDefault extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(TaskException.class)
    public ResponseEntity<String> handleTaskException(
            @NotNull TaskException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({ModelNotFound.class})
    public ResponseEntity<String> handleResourceNotFoundException
            (@NotNull ModelNotFound e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FileException.class)
    public ResponseEntity<String> handleFileException
            (@NotNull FileException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DocumentException.class)
    public ResponseEntity<String> handleDocumentException
            (@NotNull DocumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleNotificationException
            (@NotNull NotificationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ArchiveException.class)
    public ResponseEntity<String> handleArchiveException
            (@NotNull ArchiveException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({InvalidKeyException.class,
            NoSuchAlgorithmException.class,
            NoSuchProviderException.class, SignatureException.class, IOException.class})
    public ResponseEntity<String> handleEDSException() {
        return new ResponseEntity<>("No signature document", HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
                                 @NotNull HttpHeaders headers,
                                 @NotNull HttpStatusCode status,
                                 @NotNull WebRequest request) {
        Map<String, Object> objectBody = new LinkedHashMap<>();
        objectBody.put("Current Timestamp", new Date());
        objectBody.put("Status", status.value());
        objectBody.put("Name", "Validate");
        List<String> exceptionalErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        objectBody.put("Errors", exceptionalErrors);
        return new ResponseEntity<>(objectBody, status);
    }
}
