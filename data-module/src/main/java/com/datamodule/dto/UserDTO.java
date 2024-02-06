package com.datamodule.dto;

import com.datamodule.models.enums.ERole;
import lombok.*;

import java.io.Serializable;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO  implements Serializable {

    private Long idUser;

    private String username;

    private String password;

    private EmployeeDTO employeeDTO;

    private ERole eRole;
}
