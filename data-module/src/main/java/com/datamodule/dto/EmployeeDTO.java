package com.datamodule.dto;


import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {

    private Long idEmployee;

    private Long idUser;

    private String name;

    private String surname;

    private String patronymic;
}
