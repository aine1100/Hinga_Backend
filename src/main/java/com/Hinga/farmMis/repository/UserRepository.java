package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Integer> {

    Users findByEmail(String email);
    Users findByResetPasswordToken(String resetToken);
    Users findById(Long id);

}
