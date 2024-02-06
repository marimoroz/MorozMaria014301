package com.logicmodule.service.task;

import com.datamodule.exeptions.ModelNotFound;

import java.util.LinkedHashMap;

public interface TaskStatistic {

    double percentExecute(Long id_task) throws ModelNotFound;

    LinkedHashMap<String,Double> getAllStatisticByTask(Long id_document) throws ModelNotFound;

    double percentRollBack(Long id_document) throws ModelNotFound;

}
