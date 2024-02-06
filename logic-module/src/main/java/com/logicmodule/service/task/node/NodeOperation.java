package com.logicmodule.service.task.node;

import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Employee;
import com.datamodule.models.SequentialTaskNode;

import java.util.LinkedList;

public interface NodeOperation {
    SequentialTaskNode createSequentialTaskNode(Long id_employee, Long prevNode
            , Long nextNode, String name_node,String name_desc, LinkedList<Employee> employeesModel
            , long count) throws ModelNotFound;


}
