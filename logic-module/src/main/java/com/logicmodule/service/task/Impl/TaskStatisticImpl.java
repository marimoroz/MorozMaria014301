package com.logicmodule.service.task.Impl;

import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.SequentialTaskNode;
import com.datamodule.models.Task;
import com.logicmodule.service.document.DocumentOperation;
import com.logicmodule.service.task.TaskStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Service("TaskStatisticImpl")
@RequiredArgsConstructor
@Slf4j
public class TaskStatisticImpl implements TaskStatistic {

    private final TaskServiceImpl taskService;

    private final DocumentOperation documentOperation;

    @Override
    @Transactional
    public double percentExecute(Long id_task) throws ModelNotFound {
        var task = taskService.findTaskByID(id_task);
        return this.percentExecute(task);
    }

    private double percentExecute(Task task) {
        var secNodeTasks = task.getSequentialTaskNodes();
        int countDone = 0;
        for (SequentialTaskNode s : secNodeTasks) {
            if (s.getIsDone()) {
                countDone++;
            }
        }
        return (double) countDone / secNodeTasks.size();
    }

    @Override
    @Transactional
    public LinkedHashMap<String, Double> getAllStatisticByTask(Long id_document) throws ModelNotFound {
        var doc = documentOperation.findDocumentByIDWithTasks(id_document);
        var result = new LinkedHashMap<String, Double>();
        var tasks = new LinkedList<Task>();
        tasks.addAll(doc.getDoneTask());
        tasks.addAll(doc.getWaitTasks());
        var executeTask = doc.getExecuteTask();
        if (executeTask != null) {
            tasks.add(executeTask);
        }
        for (Task task : tasks) {
            var res = this.percentExecute(task);
            result.put(task.getNameTask(), res);
        }
        return result;
    }

    @Override
    @Transactional
    public double percentRollBack(Long id_document) throws ModelNotFound {
        var taskRoleBackSize = taskService
                .findTasksByIdDocumentWithStatusRoll_Back(id_document).size();
        log.info(taskRoleBackSize+"himiya");
        var doc = documentOperation.findDocumentByIDWithTasks(id_document);
        var tasks = new LinkedList<Task>();
        tasks.addAll(doc.getDoneTask());
        tasks.addAll(doc.getWaitTasks());
        var executeTask = doc.getExecuteTask();
        if (executeTask != null) {
            tasks.add(executeTask);
        }
        if (!tasks.isEmpty()) {
            return (double) taskRoleBackSize / tasks.size();
        }
        return 100;
    }
}
