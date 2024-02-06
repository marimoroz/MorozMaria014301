package com.datamodule.repository;

import com.datamodule.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository(value = "DocumentRepository")
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.waitTasks" +
            " WHERE d.idDocument =:idDocument")
    Optional<Document> findDocumentByIdDocumentWithTasksWait(
            @Param("idDocument") Long idDocument);


    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.doneTask WHERE d.idDocument =:idDocument")
    Optional<Document> findDocumentByIdDocumentWithTasksDone(
            @Param("idDocument") Long idDocument);


    @Query("SELECT DISTINCT d FROM Document d " +
            "LEFT JOIN FETCH  d.doneTask " +
            "LEFT JOIN FETCH d.executeTask " +
            "WHERE d.idDocument IN :docIds")
    List<Document> findDocumentsByIdUserIn(List<Long> docIds);

    List<Document> findDocumentsByIsArchive(Boolean isArchive);
}
