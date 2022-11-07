package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.CustomerCreateDto;
import com.scisk.sciskbackend.dto.EmployeeCreateDto;
import com.scisk.sciskbackend.dto.UserReturnDto;
import com.scisk.sciskbackend.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    UserReturnDto createCustomerAccount(CustomerCreateDto customerCreateDto);

    UserReturnDto createEmployeeAccount(EmployeeCreateDto employeeCreateDto);

    Page<UserReturnDto> findAllUserByFilters(Integer page, Integer size, String firstname, String lastname, String email, String role, String status, String city, String address, String country);

    UserReturnDto update(Long userIdValue, UserUpdateDto userUpdateDto);

    UserReturnDto findById(Long id);

    void changeUserPassword(Long userIdValue, String password);

    UserReturnDto getConnectedUser();
}
