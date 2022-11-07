package com.scisk.sciskbackend.util;

import com.scisk.sciskbackend.entity.User;

import java.util.Optional;

public interface AuthenticationUtil {
    Optional<User> getConnectedUser();
}
