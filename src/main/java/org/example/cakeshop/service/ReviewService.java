package org.example.cakeshop.service;

import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.entity.Review;
import org.example.cakeshop.exception.AppException;
import org.example.cakeshop.exception.NotFoundException;
import org.example.cakeshop.repository.CakeRepository;
import org.example.cakeshop.repository.CustomerRepository;
import org.example.cakeshop.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private static final int MAX_REVIEWS_IN_CATALOG = 40;

    private final ReviewRepository reviewRepository;
    private final CakeRepository cakeRepository;
    private final CustomerRepository customerRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         CakeRepository cakeRepository,
                         CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.cakeRepository = cakeRepository;
        this.customerRepository = customerRepository;
    }

    // Отзывы о тортах мастера
    @Transactional(readOnly = true)
    public List<Review> findRecentForConfectioner(Long confectionerId) {
        return reviewRepository.findRecentForConfectionerCatalog(confectionerId).stream()
                .limit(MAX_REVIEWS_IN_CATALOG)
                .toList();
    }

    @Transactional
    public Review add(Long customerId, Long confectionerId, Long cakeId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new AppException("Оценка должна быть от 1 до 5.");
        }
        Cake cake = cakeRepository.findById(cakeId)
                .orElseThrow(() -> new NotFoundException("Торт не найден"));
        cake.getConfectioners().size();
        boolean belongs = cake.getConfectioners().stream()
                .anyMatch(k -> k.getId().equals(confectionerId));
        if (!belongs) {
            throw new AppException("Торт не у этого кондитера.");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Review r = new Review();
        r.setCustomer(customer);
        r.setCake(cake);
        r.setRating(rating);
        String c = comment == null ? null : comment.trim();
        r.setComment(c != null && c.isEmpty() ? null : c);
        r.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(r);
    }
}
