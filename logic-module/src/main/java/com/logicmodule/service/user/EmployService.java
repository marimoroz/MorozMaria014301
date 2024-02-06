package com.logicmodule.service.user;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployService {

    EmployeeDTO getEmployeeById(Long id_employee) throws ModelNotFound;

    Employee getEmployeeModelById(Long id_employee) throws ModelNotFound;

    EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) throws ModelNotFound;

    EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) throws ModelNotFound;

    Page<EmployeeDTO> getEmployeesFromPage(Pageable pageable);
}
