package com.datamodule.repository;


import com.datamodule.models.enums.ERole;
import com.datamodule.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository(value = "UserRepository")
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    @Transactional
    Optional<User> findUserByUsername(@Param("username")String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.notifications WHERE u.username = :idUser")
    Optional<User> findUserByIdUserWithNotifications(@Param("idUser")Long idUser);

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.idUser IN :userIds AND u.eRole = :roleName")
    List<User> findByIdUserInAndRoleName(List<Long> userIds, ERole roleName);

    @Query("SELECT DISTINCT u FROM User u WHERE u.eRole = :roleName")
    List<User> findByRoleName(@Param("roleName") ERole roleName);

    Boolean existsUserByUsername(String username);
}
