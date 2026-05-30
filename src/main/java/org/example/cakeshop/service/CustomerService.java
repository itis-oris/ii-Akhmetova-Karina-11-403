package org.example.cakeshop.service;

import org.example.cakeshop.dto.RegisterRequest;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.entity.Role;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.repository.CustomerRepository;
import org.example.cakeshop.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressLocationService addressLocationService;

    public CustomerService(CustomerRepository customerRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AddressLocationService addressLocationService) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressLocationService = addressLocationService;
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Transactional
    public Customer register(RegisterRequest request) {
        String email = request.getEmail() == null ? null : request.getEmail().trim();
        String username = request.getUsername() == null ? null : request.getUsername().trim();

        if (email == null || email.isBlank() || username == null || username.isBlank()) {
            throw new AppException("Email и username обязательны");
        }

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new AppException("Email уже используется");
        }
        if (customerRepository.existsByUsernameIgnoreCase(username)) {
            throw new AppException("Username уже используется");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Customer c = new Customer();
        c.setUsername(username);
        c.setEmail(email);
        c.setPassword(passwordEncoder.encode(request.getPassword()));
        c.setRoles(Set.of(userRole));
        return customerRepository.save(c);
    }

    @Transactional
    public void updateProfile(Long customerId, String phone, String address) {
        Customer c = customerRepository.findById(customerId).orElse(null);
        if (c == null) {
            return;
        }
        c.setPhone(trimToNull(phone));
        String addr = address == null ? null : address.trim();
        c.setAddress(addr == null || addr.isEmpty() ? null : addr);
        c.setLatitude(null);
        c.setLongitude(null);
        if (c.getAddress() != null) {
            addressLocationService.geocode(c.getAddress()).ifPresent(p -> {
                c.setLatitude(p.getLatitude());
                c.setLongitude(p.getLongitude());
            });
        }
        customerRepository.saveAndFlush(c);
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
