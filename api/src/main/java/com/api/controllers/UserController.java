package com.api.controllers;

import com.datamodule.dto.AuthenticationDTO;
import com.datamodule.dto.UserDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.logicmodule.exeptions.RegisterException;
import com.logicmodule.service.eds.EdsService;
import com.logicmodule.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@CrossOrigin
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService authenticationService;


    @PostMapping("/authorization")
    public ResponseEntity<AuthenticationDTO> authorization(
            @ModelAttribute AuthenticationDTO authenticationDTO) throws ModelNotFound {
        return new ResponseEntity<>
                (authenticationService.authentication(authenticationDTO), HttpStatus.OK);
    }

    @GetMapping("/keys")
    public ResponseEntity<Pair<byte[], byte[]>> get_keys() throws NoSuchAlgorithmException {
        var key = EdsService.generateKeyPair();
        var pair = Pair.of(EdsService
                .privateKeyToBytes(key.getPrivate()),
                EdsService.publicKeyToBytes(key.getPublic()));
        return ResponseEntity.ok(pair);
    }

    @PostMapping("/registrationNewUser")
    public ResponseEntity<AuthenticationDTO> registrationNewUser(
            @ModelAttribute UserDTO registerRequest) throws NoSuchAlgorithmException,
            RegisterException {
        return new ResponseEntity<>
                (authenticationService.registerNewUser(registerRequest), HttpStatus.CREATED);
    }
}
