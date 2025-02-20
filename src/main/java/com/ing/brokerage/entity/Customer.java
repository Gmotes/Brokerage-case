package com.ing.brokerage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Username is required")
  @Column(unique = true)
  private String username;

  @Email
  @NotBlank(message = "Email is required")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
}

