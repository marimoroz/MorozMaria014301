package com.logicmodule.service.task.node.Impl;

import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Employee;
import com.datamodule.models.SequentialTaskNode;
import com.logicmodule.service.task.node.NodeOperation;
import com.logicmodule.service.user.EmployService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service(value = "NodeOperationImpl")
@RequiredArgsConstructor
public class NodeOperationImpl implements NodeOperation {

    private final EmployService employService;

    @Override
    public SequentialTaskNode createSequentialTaskNode(Long id_employee, Long prevNode,
                                                       Long nextNode, String name_node, String name_desc
            , LinkedList<Employee> employeesModel, long count) throws ModelNotFound {
        var employee = employService.getEmployeeModelById(id_employee);
        employeesModel.add(employee);
        return SequentialTaskNode
                .builder()
                .count(count)
                .prevNode(prevNode)
                .documentState(null)
                .name_node(name_node)
                .name_desc(name_desc)
                .employee(employee)
                .nextNode(nextNode)
                .can_be_done(false)
                .isDone(false)
                .build();
    }
}
