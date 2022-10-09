package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.UserDS;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserInputDSImpl implements UserInputDS {

    private final UserRepository userRepository;

    public UserInputDSImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> existsByEmail(String email) {
        return userRepository.existsByEmail(email).stream().map(UserDS::map).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDS::map);
    }

    @Override
    public User save(User user) {
        userRepository.save(UserDS.map(user));
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(UserDS::map);
    }
}
