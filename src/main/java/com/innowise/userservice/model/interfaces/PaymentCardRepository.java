package com.innowise.userservice.model.interfaces;

import com.innowise.userservice.model.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    @Query(value = "SELECT c.* FROM payment_cards c JOIN users u ON c.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
    List<PaymentCard> findPaymentCardsByUserId(Long userId);

    default PaymentCard activateCard(Long id) {
        PaymentCard card = findById(id).orElseThrow();
        card.setActive(true);
        return card;
    }

    default PaymentCard deactivateCard(Long id) {
        PaymentCard card = findById(id).orElseThrow();
        card.setActive(false);
        return card;
    }
}
