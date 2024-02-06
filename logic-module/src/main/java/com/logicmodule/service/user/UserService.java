package com.logicmodule.service.user;

import com.datamodule.dto.AuthenticationDTO;
import com.datamodule.dto.UserDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.User;
import com.datamodule.models.enums.ERole;
import com.logicmodule.exeptions.RegisterException;


import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {
    User findUserById(Long id_user) throws ModelNotFound;
    List<User> findByIdUserInAndRoleName(List<Long> id_users, ERole eRole);

    AuthenticationDTO registerNewUser(UserDTO registerRequest) throws RegisterException, NoSuchAlgorithmException;
    AuthenticationDTO authentication(AuthenticationDTO authenticationRequest) throws ModelNotFound;
}
