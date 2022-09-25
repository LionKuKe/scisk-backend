package com.scisk.sciskbackend.util;

import com.scisk.sciskbackend.config.springsecurity.UserDetailsImpl;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
public class AuthenticationUtilServImpl implements AuthenticationUtil {

    private final UserRepository userRepository;

    public AuthenticationUtilServImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getConnectedUser() {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        if (authentication.isEmpty()) return Optional.empty();

        Object principal = authentication.get().getPrincipal();
        String email;
        if (principal instanceof String) {
            email = principal.toString();
        } else {
            email = ((UserDetailsImpl) principal).getEmail();
        }

        log.info("connected user : " + email);
        return userRepository.findByEmail(email);
    }
}
