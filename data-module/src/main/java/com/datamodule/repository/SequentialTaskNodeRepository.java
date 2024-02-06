package com.datamodule.repository;

import com.datamodule.models.SequentialTaskNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.Optional;

@Repository(value = "SequentialTaskNodeRepository")
public interface SequentialTaskNodeRepository extends
        JpaRepository<SequentialTaskNode, Long> {

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = true AND " +
            "stn.isDone = true AND stn.task.idTask = :idTask ORDER BY stn.count")
    LinkedList<SequentialTaskNode> findDoneSequentialTaskNodesByTaskId(
            @Param("idTask") Long idTask);

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = true AND " +
            "stn.isDone = true AND stn.task.employee.user.idUser= :idUser ORDER BY stn.count")
    LinkedList<SequentialTaskNode> findDoneSequentialTaskNodesByUserId(
            @Param("idUser") Long idUser);

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = true AND " +
            "stn.isDone = false AND stn.task.idTask = :idTask AND stn.employee.user.idUser =:idUser")
    Optional<SequentialTaskNode> findExecuteSequentialTaskNodesByTaskIdWithUserOne
            (@Param("idTask") Long idTask, @Param("idUser") Long idUser);


    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = false AND " +
            "stn.isDone = false AND stn.employee.user.idUser =:idUser ORDER BY stn.count")
    LinkedList<SequentialTaskNode> findWaitSequentialTaskNodesByTaskIdUser(
             @Param("idUser") Long idUser);

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = true AND " +
            "stn.isDone = false AND stn.employee.user.idUser =:idUser ORDER BY stn.count")
    LinkedList<SequentialTaskNode> findExecuteSequentialTaskNodesByUserId(
            @Param("idUser") Long idUser);

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done = true AND " +
            "stn.isDone = false AND stn.task.idTask = :idTask")
    Optional<SequentialTaskNode> findExecuteSequentialTaskNodesByTaskId
            (@Param("idTask") Long idTask);

    @Query("SELECT stn FROM SequentialTaskNode stn WHERE stn.can_be_done =:can_be_done AND " +
            "stn.isDone = :isDone AND stn.employee.user.idUser= :idUser")
    LinkedList<SequentialTaskNode> findSequentialTaskNodesByUserId(
            @Param("idUser") Long idUser, @Param("isDone") Boolean isDone, @Param("can_be_done")
    Boolean can_be_done);
}
