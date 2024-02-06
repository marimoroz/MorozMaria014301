package com.logicmodule.service.user.Impl;

import com.datamodule.dto.AuthenticationDTO;
import com.datamodule.dto.UserDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.PublishKey;
import com.datamodule.models.User;
import com.datamodule.models.enums.ERole;
import com.datamodule.repository.EmployeeRepository;
import com.datamodule.repository.PublishKeysRepository;
import com.datamodule.repository.UserRepository;
import com.logicmodule.exeptions.RegisterException;
import com.logicmodule.mappers.EmployeeMapper;
import com.logicmodule.service.eds.EdsService;
import com.logicmodule.service.user.UserService;
import com.security.service.JwtService;
import com.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service(value = "UserService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EmployeeMapper employeeMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final EmployeeRepository employeeRepository;

    private final AuthenticationManager authenticationManager;

    private final PublishKeysRepository publishKeysRepository;

    @Override
    public User findUserById(Long id_user) throws ModelNotFound {
        return userRepository.findById(id_user)
                .orElseThrow(() -> new ModelNotFound("this user with id: " + id_user +
                        "not found"));
    }

    @Override
    public List<User> findByIdUserInAndRoleName(List<Long> id_users, ERole eRole) {
        return userRepository.findByIdUserInAndRoleName(id_users, eRole);
    }



    @Override
    public AuthenticationDTO registerNewUser(UserDTO registerRequest) throws RegisterException,
            NoSuchAlgorithmException {
        var keys = EdsService.generateKeyPair();
        var employ = employeeMapper.fromDTO(registerRequest.getEmployeeDTO());
        var publicKey = PublishKey.builder()
                .publishKey(EdsService
                        .publicKeyToBytes(keys.getPublic())).build();
        var user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .privateKey(EdsService.privateKeyToBytes(keys.getPrivate()))
                .eRole(ERole.ROLE_USER)
                .build();
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new RegisterException("User exists");
        }
        employ.setUser(user);
        publicKey.setUser(user);
        userRepository.save(user);
        employ = employeeRepository.save(employ);
        user.setEmployee(employ);
        publicKey = publishKeysRepository.save(publicKey);
        user.setPublishKey(publicKey);
        user = userRepository.save(user);
        var jwtToken = jwtService.generateToken(UserDetailsImpl.build(user));
        return AuthenticationDTO.builder()
                .token(jwtToken)
                .username(user.getUsername()).build();
    }

    @Transactional
    @Override
    public AuthenticationDTO authentication(AuthenticationDTO authenticationDTO)
            throws ModelNotFound {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationDTO.getUsername(), authenticationDTO.getPassword())
        );
        var user = userRepository.findUserByUsername(authenticationDTO.getUsername())
                .orElseThrow(() -> new ModelNotFound("User not found"));
        var jwtToken = jwtService.generateToken(UserDetailsImpl.build(user));
        return AuthenticationDTO.builder()
                .id_employee(user.getEmployee().getIdEmployee())
                .token(jwtToken)
                .username(user.getUsername()).build();
    }
}
