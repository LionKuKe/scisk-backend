package com.scisk.sciskbackend.config.springsecurity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.util.GlobalParams;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String lastname;
    private String firstname;
    private String email;
    private String status;
    private String phone1;
    private String phone2;
    private String phone3;
    private String country;
    private String city;
    private String address;
    private Boolean employee;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            Long id,
            String lastname,
            String firstname,
            String email,
            String password,
            String status,
            String phone1,
            String phone2,
            String phone3,
            String country,
            String city,
            String address,
            Boolean employee,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.password = password;
        this.status = status;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.phone3 = phone3;
        this.country = country;
        this.city = city;
        this.address = address;
        this.employee = employee;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        return new UserDetailsImpl(
                user.getId(),
                user.getLastname(),
                user.getFirstname(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus(),
                user.getPhone1(),
                user.getPhone2(),
                user.getPhone3(),
                user.getCountry(),
                user.getCity(),
                user.getAddress(),
                user.getEmployee(),
                authorities
        );
    }

    @JsonIgnore
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(GlobalParams.UserStatus.ACTIVE.name());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getEmployee() {
        return employee;
    }

    public void setEmployee(Boolean employee) {
        this.employee = employee;
    }
}
