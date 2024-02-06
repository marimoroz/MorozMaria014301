package com.datamodule.repository;

import com.datamodule.models.Employee;
import com.datamodule.models.User;
import com.datamodule.models.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "EmployeeRepository")
public interface EmployeeRepository extends JpaRepository<Employee,Long>
{
    @Query("SELECT DISTINCT e FROM Employee e " +
            "WHERE e.user.eRole = :roleName")
    List<Employee> findByAllEmployeesByRoleUser(@Param("roleName") ERole roleName);
}
