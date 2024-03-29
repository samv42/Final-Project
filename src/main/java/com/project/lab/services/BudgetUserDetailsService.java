package com.project.lab.services;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Role;
import com.project.lab.repo.UserRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;


@Component
public class BudgetUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder encoder;

    public List<CustomUserDetails> getAllUsers(){
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails optionalUser = userRepo.findByUsername(username);

        if (optionalUser == null) {
            throw new UsernameNotFoundException(username + " is not a valid username! Check for typos and try again.");
        }

        return optionalUser;
    }

    @Transactional(readOnly = true)
    public CustomUserDetails getUserByUserId(Long userId) throws EntityNotFoundException {
        CustomUserDetails user = userRepo.getById(userId);

        return (CustomUserDetails) Hibernate.unproxy(user);
    }

    @Transactional(readOnly = true)
    public CustomUserDetails getUser(String username) throws EntityNotFoundException  {
        return userRepo.findByUsername(username);
    }

    public CustomUserDetails createNewUser(CustomUserDetails userDetails) {
        userDetails.setId(null);
        userDetails.getAuthorities().forEach(a -> a.setId(null));

        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        userDetails.setEnabled(true);
        if(userDetails.getAuthorities().isEmpty()) {
            userDetails.setAuthorities(Collections.singletonList(new Role(Role.Roles.ROLE_USER)));
        }

        checkPassword(userDetails.getPassword());
        userDetails.setPassword(encoder.encode(userDetails.getPassword()));
        try {
            return userRepo.save(userDetails);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    public void checkPassword(String password) {
        if (password == null) {
            throw new IllegalStateException("You must set a password");
        }
        if (password.length() < 6) {
            throw new IllegalStateException("Password is too short. Must be longer than 6 characters");
        }
    }
    public void changeUserDetails(CustomUserDetails user){
        CustomUserDetails original = getUserByUserId(user.getId());
        checkPassword(user.getPassword());
        original.setPassword(encoder.encode(user.getPassword()));
        original.setUsername(user.getUsername());
        try {
            userRepo.save(original);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }

    }
}
