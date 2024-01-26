package com.project.lab.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "incomes")
@Data
public class Income implements Serializable{
    private static final long serialVersionUID = 6527855645691638321L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String date;
    private boolean recurring;
    private double amount;

    private boolean paymentReceived;
    private String paymentDate;

    @ManyToOne (optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    public String getUser() {
        return user.getUsername();
    }
    public long getUserId() { return user.getId();}

    @Override
    public String toString() {
        return "Income{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", recurring=" + recurring +
                ", amount=" + amount +
                ", paymentReceived=" + paymentReceived +
                ", paymentDate='" + paymentDate + '\'' +
                ", account=" + account +
                ", user=" + user +
                '}';
    }
}
