package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.UserCreateDto;
import com.scisk.sciskbackend.dto.UserReturnDto;
import com.scisk.sciskbackend.entity.Role;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.repository.UserRepository;
import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.PasswordEncodingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncodingManager passwordEncodingManager;

    @Autowired
    private CounterService counterService;

    @Override
    public UserReturnDto createAccount(UserCreateDto userCreateDto) {
        // on vérifie les roles
        if (Objects.isNull(userCreateDto.getRoles()) || userCreateDto.getRoles().isEmpty() || userCreateDto.getRoles().size() ==0) {
            throw new ObjectNotFoundException("role.null");
        }

        User user = User.builder()
                .firsname(userCreateDto.getFirstName())
                .lastname(userCreateDto.getLastName())
                .email(userCreateDto.getEmail())
                .password(userCreateDto.getPassword())
                .status(GlobalParams.UserStatus.ACTIVE.name())
                .phone1(userCreateDto.getPhone1())
                .phone2(userCreateDto.getPhone2())
                .phone3(userCreateDto.getPhone3())
                .country(userCreateDto.getCountry())
                .city(userCreateDto.getCity())
                .address(userCreateDto.getAddress())
                .employee(userCreateDto.getEmployee())
                .roles(userCreateDto.getRoles().stream().map(Role::new).collect(Collectors.toList()))
                .build();

        // on teste la validité de l'adresse email
        if (!user.isEmailCorrect()) {
            throw new ObjectExistsException("incorrect.email");
        }

        // on teste l'existence de l'adresse email
        if (!userRepository.existsByEmail(userCreateDto.getEmail()).isEmpty()) {
            throw new ObjectExistsException("email.already.exists");
        }

        // on teste la validité du mot de passe
        if (!user.isPassordCorrect()) {
            throw new ObjectExistsException("incorrect.password");
        }
        user.setPassword(passwordEncodingManager.encode(user.getPassword()));

        // on créé le compte utilisateur en bd

        user.setId(counterService.getNextSequence(GlobalParams.USER_COLLECTION_NAME));
        return UserReturnDto.map(userRepository.save(user));
    }
}
