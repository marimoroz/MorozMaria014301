package com.datamodule.repository;

import com.datamodule.models.Task;
import com.datamodule.models.enums.ETypeTaskExecute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.Optional;

@Repository(value = "TaskRepository")
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.idTask = :idTask")
    Optional<Task> findTaskByIdTask(@Param("idTask") Long idTask);


    @Query("SELECT t FROM Task t WHERE t.documentExecute.idDocument = :idDocument")
    Optional<Task> findTaskByIdDocument(@Param("idDocument") Long idDocument);

    @Query("SELECT t FROM Task t WHERE t.documentExecute.idDocument = :idDocument " +
            "AND t.taskExecute =:eTypeTaskExecute")
    Optional<Task> findExecuteTaskByIdDocument(@Param("idDocument") Long idDocument,
                                               @Param("eTypeTaskExecute") ETypeTaskExecute eTypeTaskExecute);

    @Query("SELECT t FROM Task t WHERE t.documentWait.idDocument =:idDocumentWait " +
            "ORDER BY t.count")
    LinkedList<Task> findWaitTasksWithSequentialTaskNodes
            (@Param("idDocumentWait") Long idDocumentWait);

    @Query("SELECT t FROM Task t WHERE t.documentDone.idDocument =:idDocumentWait " +
            "ORDER BY t.count")
    LinkedList<Task> findDoneTasksWithSequentialTaskNodes
            (@Param("idDocumentWait") Long idDocumentWait);

    @Query("SELECT t FROM Task t WHERE t.employee.user.idUser = :idUser AND t.taskExecute = :eTypeTaskExecute ORDER BY t.count")
    LinkedList<Task> findTasksWithSequentialTaskNodesByUserId
            (@Param("idUser") Long idUser, @Param("eTypeTaskExecute") ETypeTaskExecute eTypeTaskExecute);

    @Query("SELECT t FROM Task t WHERE t.taskExecute = :eTypeTaskExecute ORDER BY t.count")
    LinkedList<Task> findTasksWithSequentialTaskNodes
            (@Param("eTypeTaskExecute") ETypeTaskExecute eTypeTaskExecute);

    @Query("SELECT t FROM Task t WHERE t.startDocument.idDocument = :idDocument AND t.taskExecute = :eTypeTaskExecute ORDER BY t.count")
    LinkedList<Task> findTasksWithSequentialTaskNodesByDocumentId
            (@Param("idDocument") Long idDocument, @Param("eTypeTaskExecute") ETypeTaskExecute eTypeTaskExecute);

    @Query("SELECT t FROM Task t WHERE " +
            "t.taskExecute =:eTypeTaskExecute AND t.documentExecute != null " +
            "AND t.documentExecute.idDocument =:idDocumentExecute")
    LinkedList<Task> findExecuteTasks(@Param("eTypeTaskExecute") ETypeTaskExecute eTypeTaskExecute
            ,@Param("idDocumentExecute") Long idDocumentExecute);

}
