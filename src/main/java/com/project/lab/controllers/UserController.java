package com.project.lab.controllers;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Role;
import com.project.lab.services.BudgetUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String showNewUserPage(Model model) {
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

    @GetMapping("/edit-user")
    public String showEditUserPage(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails updateUser = new CustomUserDetails();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        model.addAttribute("user", updateUser);
        return "edit-user";
    }

    @PostMapping(value = "/update-user")
    public String updateUser(@ModelAttribute("user") CustomUserDetails user) {
        userDetailsService.changeUserDetails(user);
        return "redirect:/";
    }

}
