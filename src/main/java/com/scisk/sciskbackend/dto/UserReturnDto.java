package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Role;
import com.scisk.sciskbackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReturnDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String status;
    private String phone1;
    private String phone2;
    private String phone3;
    private String country;
    private String city;
    private String address;
    private Boolean employee;
    private List<String> roles;

    public static UserReturnDto map(User user) {
        return UserReturnDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .status(user.getStatus())
                .phone1(user.getPhone1())
                .phone2(user.getPhone2())
                .phone3(user.getPhone3())
                .country(user.getCountry())
                .city(user.getCity())
                .address(user.getAddress())
                .employee(user.getEmployee())
                .build();
    }
}
