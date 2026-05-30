package org.example.cakeshop.repository;

import org.example.cakeshop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    //юзер по email
    Optional<Customer> findByEmailIgnoreCase(String email);
    //юзер по имени
    Optional<Customer> findByUsernameIgnoreCase(String username);
    //существует ли юзер с таким email
    boolean existsByEmailIgnoreCase(String email);
    //существует ли юзер с таким именем
    boolean existsByUsernameIgnoreCase(String username);
}