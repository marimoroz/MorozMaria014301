package com.datamodule.dto;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDTO implements Serializable {

    private String username;

    private String token;

    private Long id_employee;

    private String password;
}
