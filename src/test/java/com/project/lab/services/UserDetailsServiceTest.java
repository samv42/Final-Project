package com.project.lab.services;

import com.project.lab.models.Account;
import com.project.lab.models.CustomUserDetails;
import com.project.lab.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;

@SpringBootTest(classes = BudgetUserDetailsService.class)
@WithMockUser(value = "user")
public class UserDetailsServiceTest {
    @Autowired
    BudgetUserDetailsService budgetUserDetailsService;

    @MockBean
    UserRepo userRepo;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<Account> argumentCaptor;

    @Test
    public void createNewUser() throws Exception{
        CustomUserDetails user1 = CustomUserDetails.builder()
                .id((long)1)
                .username("user")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        when(userRepo.save(any())).thenReturn(user1);
        CustomUserDetails actual = budgetUserDetailsService.createNewUser(user1);
        assertEquals(user1, actual);
    }
    @Test
    public void changeUserDetails() throws Exception {
        CustomUserDetails user1 = CustomUserDetails.builder()
                .id((long)1)
                .username("user")
                .password("password")
                .build();
        CustomUserDetails user2 = CustomUserDetails.builder()
                .id((long)1)
                .username("user2")
                .password("password")
                .build();
        when(budgetUserDetailsService.getUserByUserId(anyLong())).thenReturn(user1);
        budgetUserDetailsService.changeUserDetails(user2);
    }

    @Test
    public void checkPasswordFailure() {
        String password = "short";
        Exception e = assertThrows(IllegalStateException.class, ()-> budgetUserDetailsService.checkPassword(password));
        assertEquals("Password is too short. Must be longer than 6 characters", e.getMessage());
    }
}
