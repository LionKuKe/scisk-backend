package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.CustomerCreateDto;
import com.scisk.sciskbackend.dto.EmployeeCreateDto;
import com.scisk.sciskbackend.dto.UserReturnDto;
import com.scisk.sciskbackend.dto.UserUpdateDto;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.exception.ObjectExistsException;
import com.scisk.sciskbackend.exception.ObjectNotFoundException;
import com.scisk.sciskbackend.inputdatasource.UserInputDS;
import com.scisk.sciskbackend.util.AuthenticationUtil;
import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.PasswordEncodingManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServImpl implements UserService {

    private UserInputDS userInputDS;
    private PasswordEncodingManager passwordEncodingManager;
    private CounterService counterService;
    private final MongoTemplate mongoTemplate;
    private final AuthenticationUtil authenticationUtil;

    public UserServImpl(
            UserInputDS userInputDS,
            PasswordEncodingManager passwordEncodingManager,
            CounterService counterService,
            MongoTemplate mongoTemplate,
            AuthenticationUtil authenticationUtil
    ) {
        this.userInputDS = userInputDS;
        this.passwordEncodingManager = passwordEncodingManager;
        this.counterService = counterService;
        this.mongoTemplate = mongoTemplate;
        this.authenticationUtil = authenticationUtil;
    }

    @Override
    public UserReturnDto createCustomerAccount(CustomerCreateDto customerCreateDto) {
        User user = User.builder()
                .firstname(customerCreateDto.getFirstName())
                .lastname(customerCreateDto.getLastName())
                .email(customerCreateDto.getEmail())
                .password(customerCreateDto.getPassword())
                .status(GlobalParams.UserStatus.ACTIVE.name())
                .phone1(customerCreateDto.getPhone1())
                .phone2(customerCreateDto.getPhone2())
                .phone3(customerCreateDto.getPhone3())
                .country(customerCreateDto.getCountry())
                .city(customerCreateDto.getCity())
                .address(customerCreateDto.getAddress())
                .employee(false)
                .roles(Collections.singletonList(GlobalParams.Role.CUSTOMER.name()))
                .build();

        // on teste la validité de l'adresse email
        if (!user.isEmailCorrect()) {
            throw new ObjectExistsException("incorrect.email");
        }

        // on teste l'existence de l'adresse email
        if (!userInputDS.existsByEmail(customerCreateDto.getEmail()).isEmpty()) {
            throw new ObjectExistsException("email.already.exists");
        }

        // on teste la validité du mot de passe
        if (!user.isPassordCorrect()) {
            throw new ObjectExistsException("incorrect.password");
        }
        user.setPassword(passwordEncodingManager.encode(user.getPassword()));

        // on créé le compte utilisateur en bd

        user.setId(counterService.getNextSequence(GlobalParams.USER_COLLECTION_NAME));
        return UserReturnDto.map(userInputDS.save(user));
    }

    @Override
    public UserReturnDto createEmployeeAccount(EmployeeCreateDto employeeCreateDto) {
        User user = User.builder()
                .firstname(employeeCreateDto.getFirstName())
                .lastname(employeeCreateDto.getLastName())
                .email(employeeCreateDto.getEmail())
                .password(employeeCreateDto.getPassword())
                .status(GlobalParams.UserStatus.ACTIVE.name())
                .phone1(employeeCreateDto.getPhone1())
                .phone2(employeeCreateDto.getPhone2())
                .phone3(employeeCreateDto.getPhone3())
                .country(employeeCreateDto.getCountry())
                .city(employeeCreateDto.getCity())
                .address(employeeCreateDto.getAddress())
                .employee(true)
                .build();

        // on vérifie les roles
        AtomicBoolean reject = new AtomicBoolean(false);
        employeeCreateDto.getRoles().forEach(
                role -> {
                    if (Arrays.stream(GlobalParams.Role.values()).filter(ro -> ro.name().equals(role)).findFirst().isEmpty()) {
                        reject.set(true);
                    }
                }
        );
        if (reject.get()) {
            throw new ObjectExistsException("role.not.found");
        }
        user.setRoles(employeeCreateDto.getRoles());

        // on teste la validité de l'adresse email
        if (!user.isEmailCorrect()) {
            throw new ObjectExistsException("incorrect.email");
        }

        // on teste l'existence de l'adresse email
        if (!userInputDS.existsByEmail(employeeCreateDto.getEmail()).isEmpty()) {
            throw new ObjectExistsException("email.already.exists");
        }

        // on teste la validité du mot de passe
        if (!user.isPassordCorrect()) {
            throw new ObjectExistsException("incorrect.password");
        }
        user.setPassword(passwordEncodingManager.encode(user.getPassword()));

        // on créé le compte utilisateur en bd
        user.setId(counterService.getNextSequence(GlobalParams.USER_COLLECTION_NAME));
        return UserReturnDto.map(userInputDS.save(user));
    }

    @Override
    public Page<UserReturnDto> findAllUserByFilters(
            Integer page,
            Integer size,
            String firstname,
            String lastname,
            String email,
            String role,
            String status,
            String city,
            String address,
            String country
    ) {
        Pageable pageable = PageRequest.of(
                Objects.isNull(page) ? 0 : page - 1,
                Objects.isNull(size) ? GlobalParams.GLOBAL_DEFAULT_PAGE_SIZE : size
        );

        Query query = new Query();
        query.with(pageable);
        Criteria criteria = new Criteria();

        if (StringUtils.isNotBlank(firstname)) {
            criteria.and("firstname").regex(firstname, "i");
        }

        if (StringUtils.isNotBlank(lastname)) {
            criteria.and("lastname").regex(lastname, "i");
        }

        if (StringUtils.isNotBlank(email)) {
            criteria.and("email").regex(email, "i");
        }

        if (StringUtils.isNotBlank(status)) {
            criteria.and("status").regex(status, "i");
        }

        if (StringUtils.isNotBlank(city)) {
            criteria.and("city").regex(city, "i");
        }

        if (StringUtils.isNotBlank(address)) {
            criteria.and("address").regex(address, "i");
        }

        if (StringUtils.isNotBlank(country)) {
            criteria.and("country").regex(country, "i");
        }

        if (StringUtils.isNotBlank(role)) {
            //criteria.and("roles").elemMatch(Criteria.where("_id").is(userId));
            criteria.and("roles").regex(role, "i");
        }

        query.addCriteria(criteria);
        List<User> user = mongoTemplate.find(query, User.class);

        return new PageImpl<>(
                user.stream().map(UserReturnDto::map).collect(Collectors.toList()),
                pageable,
                mongoTemplate.count(new Query(criteria), User.class)
        );
    }

    @Override
    public UserReturnDto update(Long id, UserUpdateDto userUpdateDto) {
        // on vérifie l'id
        User user = userInputDS.findById(id).orElseThrow(() -> new ObjectNotFoundException("id.not.found"));

        user.setFirstname(userUpdateDto.getFirstname());
        user.setLastname(userUpdateDto.getLastname());
        user.setEmail(userUpdateDto.getEmail());
        user.setPhone1(userUpdateDto.getPhone1());
        user.setPhone2(userUpdateDto.getPhone2());
        user.setPhone3(userUpdateDto.getPhone3());
        user.setCountry(userUpdateDto.getCountry());
        user.setCity(userUpdateDto.getCity());
        user.setAddress(userUpdateDto.getAddress());
        user.setEmployee(userUpdateDto.getEmployee());

        // on vérifie les roles
        AtomicBoolean reject = new AtomicBoolean(false);
        userUpdateDto.getRoles().forEach(
                role -> {
                    if (Arrays.stream(GlobalParams.Role.values()).filter(ro -> ro.name().equals(role)).findFirst().isEmpty()) {
                        reject.set(true);
                    }
                }
        );
        if (reject.get()) {
            throw new ObjectExistsException("role.not.found");
        }
        user.setRoles(userUpdateDto.getRoles());

        // on teste la validité de l'adresse email
        if (!user.isEmailCorrect()) {
            throw new ObjectExistsException("incorrect.email");
        }

        // on teste l'existence de l'adresse email
        List<User> userList = userInputDS.existsByEmail(userUpdateDto.getEmail());
        if (!userList.isEmpty() && !userList.get(0).getId().equals(id)) {
            throw new ObjectExistsException("email.already.exists");
        }

        return UserReturnDto.map(userInputDS.save(user));
    }

    @Override
    public UserReturnDto findById(Long id) {
        return UserReturnDto.map(
                userInputDS.findById(id).orElseThrow(() -> new ObjectNotFoundException("id"))
        );
    }

    @Override
    public void changeUserPassword(Long userIdValue, String password) {
        // on vérifie l'id
        User user = userInputDS.findById(userIdValue).orElseThrow(() -> new ObjectNotFoundException("id.not.found"));

        // on teste la validité du mot de passe
        user.setPassword(password);
        if (!user.isPassordCorrect()) {
            throw new ObjectExistsException("incorrect.password");
        }
        user.setPassword(passwordEncodingManager.encode(user.getPassword()));
        userInputDS.save(user);
    }

    @Override
    public UserReturnDto getConnectedUser() {
        User user = authenticationUtil.getConnectedUser().orElseThrow(() -> new ObjectNotFoundException("connected.user.not.found"));
        return UserReturnDto.map(
                userInputDS.findById(user.getId()).orElseThrow(() -> new ObjectNotFoundException("id"))
        );
    }
}
