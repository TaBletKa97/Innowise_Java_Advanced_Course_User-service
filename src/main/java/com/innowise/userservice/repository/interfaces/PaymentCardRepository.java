package com.innowise.userservice.repository.interfaces;

import com.innowise.userservice.repository.entity.PaymentCard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    @EntityGraph(attributePaths = "user")
    List<PaymentCard> findAll();

    @EntityGraph(attributePaths = "user")
    Optional<PaymentCard> findById(Long id);

    @Query(value = "SELECT c.* FROM payment_cards c JOIN users u ON c.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
    List<PaymentCard> findPaymentCardsByUserId(Long userId);
}