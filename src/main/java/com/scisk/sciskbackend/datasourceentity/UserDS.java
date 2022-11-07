package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.Util;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.USER_COLLECTION_NAME)
public class UserDS {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String status;
    private String phone1;
    private String phone2;
    private String phone3;
    private String country;
    private String city;
    private String address;
    private Boolean employee;

    private List<String> roles;

    public boolean isEmailCorrect() {
        return Util.isEmailCorrect(email);
    }

    public boolean isPassordCorrect() {
        String passwordState = Util.getPasswordState(this.password);
        if (passwordState.equals(Util.PASSWOR_STATE.INVALID.name()) || passwordState.equals(Util.PASSWOR_STATE.WEAK.name())) {
            return false;
        } else {
            return true;
        }
    }

    public String getName() {
        return lastname + " " + firstname;
    }

    public static User map(UserDS userDS) {
        return User.builder()
                .id(userDS.getId())
                .firstname(userDS.getFirstname())
                .lastname(userDS.getLastname())
                .email(userDS.getEmail())
                .password(userDS.getPassword())
                .status(userDS.getStatus())
                .phone1(userDS.getPhone1())
                .phone2(userDS.getPhone2())
                .phone3(userDS.getPhone3())
                .country(userDS.getCountry())
                .city(userDS.getCity())
                .address(userDS.getAddress())
                .employee(userDS.getEmployee())
                .roles(userDS.getRoles())
                .build();
    }

    public static UserDS map(User user) {
        return UserDS.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus())
                .phone1(user.getPhone1())
                .phone2(user.getPhone2())
                .phone3(user.getPhone3())
                .country(user.getCountry())
                .city(user.getCity())
                .address(user.getAddress())
                .employee(user.getEmployee())
                .roles(user.getRoles())
                .build();
    }
}
