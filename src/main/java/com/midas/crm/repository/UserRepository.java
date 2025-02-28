package com.midas.crm.repository;


import com.midas.crm.entity.Role;
import com.midas.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //findBy + nombreCampo
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username); // Este método será necesario

    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    @Modifying
    @Query("update User set role=:role where username=:username")
    void updateUserRole(@Param("username") String username, @Param("role") Role role);

    Optional<User> findByUsernameOrEmail(String username,String email);
    Optional<User> findByTokenPassword(String tokenPassword);

}

