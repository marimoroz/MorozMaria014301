package com.logicmodule.service.user.Impl;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Employee;
import com.datamodule.models.enums.ERole;
import com.datamodule.repository.EmployeeRepository;
import com.logicmodule.mappers.EmployeeMapper;
import com.logicmodule.service.user.EmployService;
import com.logicmodule.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "EmployServiceImpl")
@RequiredArgsConstructor
public class EmployServiceImpl implements EmployService {

    private final EmployeeMapper employeeMapper;

    private final EmployeeRepository employeeRepository;

    private final UserService userService;


    @Override
    public EmployeeDTO getEmployeeById(Long id_employee) throws ModelNotFound {
        var employee = employeeRepository.findById(id_employee)
                .orElseThrow(() ->
                        new ModelNotFound("employee doesn't exist with id_employee: "
                                + id_employee));
        return employeeMapper.toDTO(employee);
    }

    @Override
    public Employee getEmployeeModelById(Long id_employee) throws ModelNotFound {
        return employeeRepository.findById(id_employee)
                .orElseThrow(() ->
                        new ModelNotFound("employee doesn't exist with id_employee: "
                                + id_employee));
    }

    @Override
    @Transactional
    public Page<EmployeeDTO> getEmployeesFromPage(Pageable pageable) {
        var employees = employeeRepository.findByAllEmployeesByRoleUser(ERole.ROLE_USER)
                .stream().map(employeeMapper::toDTO).toList();
        return getEmployPage(pageable, employees);
    }

    @NotNull
    private Page<EmployeeDTO> getEmployPage(Pageable pageable, List<EmployeeDTO> employeeDTOS) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, employeeDTOS.size());
        return new PageImpl<>(employeeDTOS.subList(start, end), pageable,
                employeeDTOS.size());
    }

    @Override
    public EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO) throws ModelNotFound {
        var employeeModel = employeeMapper.fromDTO(employeeDTO);
        var user = userService.findUserById(employeeDTO.getIdUser());
        employeeModel.setUser(user);
        employeeModel = employeeRepository.save(employeeModel);
        employeeDTO.setIdEmployee(employeeModel.getIdEmployee());
        return employeeDTO;
    }

    @Override
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) throws ModelNotFound {
        var employeeUpdate = getEmployeeModelById(employeeDTO.getIdEmployee());
        employeeMapper.update(employeeUpdate, employeeDTO);
        employeeRepository.save(employeeUpdate);
        return employeeDTO;
    }


}
