package org.example.cakeshop.service;

import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.entity.Order;
import org.example.cakeshop.repository.CakeRepository;
import org.example.cakeshop.repository.ConfectionerRepository;
import org.example.cakeshop.repository.CustomerRepository;
import org.example.cakeshop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ConfectionerRepository confectionerRepository;
    private final CakeRepository cakeRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ConfectionerRepository confectionerRepository,
                        CakeRepository cakeRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.confectionerRepository = confectionerRepository;
        this.cakeRepository = cakeRepository;
    }

    public List<Order> getByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdWithDetailsOrderByOrderDateDesc(customerId);
    }

    @Transactional
    public Order create(Long customerId, Long confectionerId, Long cakeId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        Confectioner confectioner = confectionerRepository.findByIdWithShop(confectionerId).orElse(null);
        Cake cake = cakeRepository.findById(cakeId).orElse(null);
        if (customer == null || confectioner == null || cake == null) {
            return null;
        }
        Order order = new Order();
        order.setCustomer(customer);
        order.setConfectioner(confectioner);
        order.setCake(cake);
        order.setTotalPrice(cake.getPrice());
        return orderRepository.save(order);
    }

    @Transactional
    public boolean deleteByOwner(Long orderId, Long customerId) {
        return orderRepository.findByIdAndCustomerId(orderId, customerId)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                })
                .orElse(false);
    }
}

