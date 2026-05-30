package org.example.cakeshop.security;

import org.example.cakeshop.repository.CustomerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    //ищем по email
    @Override
    public UserDetails loadUserByUsername(String emailInput) throws UsernameNotFoundException {
        String email = emailInput == null ? "" : emailInput.trim();
        var customer = customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        var authorities = customer.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .toList();

        //возвр польз
        return User.withUsername(customer.getEmail())
                .password(customer.getPassword())
                .authorities(authorities)
                .build();
    }
}

