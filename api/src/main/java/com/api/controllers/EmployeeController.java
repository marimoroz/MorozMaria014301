package com.api.controllers;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.logicmodule.service.user.EmployService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployService employeeService;

    @GetMapping("/get/{id_employee}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id_employee") Long id_employee) throws ModelNotFound {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(id_employee);
        return ResponseEntity.ok(employeeDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeDTO> createNewEmployee(@RequestBody EmployeeDTO employeeDTO)
            throws ModelNotFound {
        EmployeeDTO newEmployeeDTO = employeeService.createNewEmployee(employeeDTO);
        return ResponseEntity.ok(newEmployeeDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @RequestBody EmployeeDTO employeeDTO) throws ModelNotFound {
        EmployeeDTO updatedEmployeeDTO = employeeService.updateEmployee(employeeDTO);
        return ResponseEntity.ok(updatedEmployeeDTO);
    }

    @GetMapping("/getEmployeesFromPage")
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesFromPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> employeesPage = employeeService.getEmployeesFromPage(pageable);
        return ResponseEntity.ok(employeesPage);
    }
}
