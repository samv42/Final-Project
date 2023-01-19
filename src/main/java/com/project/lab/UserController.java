package com.project.lab;

import com.project.lab.models.Debt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@Controller
public class UserController {
    @Autowired
    BudgetUserDetailsService userDetailsService;

    @GetMapping("/user")
    public CustomUserDetails getUser(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @GetMapping("/new-user")
    public String showNewDebtPage(Model model) {
        CustomUserDetails user = new CustomUserDetails();
        model.addAttribute("user", user);
        return "new-user";
    }

    @PostMapping(value = "/save-user")
    public String saveUser(@ModelAttribute("user") CustomUserDetails user) {
        Role role = new Role(Role.Roles.ROLE_USER);
        user.setAuthorities(Collections.singletonList(role));
        userDetailsService.createNewUser(user);
        return "redirect:/";
    }
}
