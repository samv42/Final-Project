package com.project.lab.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "accounts")
@Data

public class Account implements Serializable {
    private static final long serialVersionUID = 6527855645691638321L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String type;
    private double balance;
    private double targetBalance;
    private double interest;
    private boolean goalReached;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    public Account(String name, String type, double balance, double targetBalance, double interest, CustomUserDetails user){
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.targetBalance = targetBalance;
        this.interest = interest;
        this.user = user;
        goalReached = false;
    }
    public String getUser() {
        return user.getUsername();
    }
    public long getUserId() { return user.getId();}
}
