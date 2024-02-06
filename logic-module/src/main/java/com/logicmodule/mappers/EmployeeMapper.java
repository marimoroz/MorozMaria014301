package com.logicmodule.mappers;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.models.Employee;

import java.util.LinkedList;


public interface EmployeeMapper {

    EmployeeDTO toDTO(Employee employee);
    LinkedList<EmployeeDTO> toDTOForLinkedList(LinkedList<Employee> employees);
    Employee fromDTO(EmployeeDTO employeeDTO);
    void update( Employee employee, EmployeeDTO employeeDTO);
}
