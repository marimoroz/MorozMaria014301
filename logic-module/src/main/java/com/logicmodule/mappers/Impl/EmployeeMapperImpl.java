package com.logicmodule.mappers.Impl;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.models.Employee;
import com.datamodule.models.User;
import com.logicmodule.mappers.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component("EmployeeMapperImpl")
@RequiredArgsConstructor
public class EmployeeMapperImpl implements EmployeeMapper {
    @Override
    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO.EmployeeDTOBuilder employeeDTO = EmployeeDTO.builder();

        if ( employee != null ) {
            employeeDTO.idUser( employeeUserIdUser( employee ) );
            employeeDTO.idEmployee( employee.getIdEmployee() );
            employeeDTO.name( employee.getName() );
            employeeDTO.surname( employee.getSurname() );
            employeeDTO.patronymic( employee.getPatronymic() );
        }

        return employeeDTO.build();
    }

    @Override
    public LinkedList<EmployeeDTO> toDTOForLinkedList(LinkedList<Employee> employees) {
        if ( employees == null ) {
            return new LinkedList<>();
        }

        LinkedList<EmployeeDTO> linkedList = new LinkedList<EmployeeDTO>();
        for ( Employee employee : employees ) {
            linkedList.add( toDTO( employee ) );
        }

        return linkedList;
    }

    @Override
    public Employee fromDTO(EmployeeDTO employeeDTO) {
        Employee.EmployeeBuilder employee = Employee.builder();

        if ( employeeDTO != null ) {
            employee.idEmployee( employeeDTO.getIdEmployee() );
            employee.name( employeeDTO.getName() );
            employee.surname( employeeDTO.getSurname() );
            employee.patronymic( employeeDTO.getPatronymic() );
        }

        return employee.build();
    }

    @Override
    public void update(Employee employee, EmployeeDTO employeeDTO) {
        if ( employeeDTO != null ) {
            employee.setName( employeeDTO.getName() );
            employee.setSurname( employeeDTO.getSurname() );
            employee.setPatronymic( employeeDTO.getPatronymic() );
        }
    }

    private Long employeeUserIdUser(Employee employee) {
        if ( employee == null ) {
            return null;
        }
        User user = employee.getUser();
        if ( user == null ) {
            return null;
        }
        Long idUser = user.getIdUser();
        if ( idUser == null ) {
            return null;
        }
        return idUser;
    }
}
