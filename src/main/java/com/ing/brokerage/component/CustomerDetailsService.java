package com.ing.brokerage.component;

import com.ing.brokerage.entity.Customer;
import com.ing.brokerage.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomerDetailsService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Autowired
  public CustomerDetailsService(final CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String role = "USER";
    if (customer.getUsername().equalsIgnoreCase("admin")) {
      role = "ADMIN";
    }

    return User.withUsername(customer.getId().toString())
        .password(customer.getPassword())
        .roles(role)
        .build();
  }
}

