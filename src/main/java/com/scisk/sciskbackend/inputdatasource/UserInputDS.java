package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserInputDS {

    List<User> existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(Long id);
}
