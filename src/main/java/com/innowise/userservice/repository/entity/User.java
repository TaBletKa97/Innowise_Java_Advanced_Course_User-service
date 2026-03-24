package com.innowise.userservice.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import com.innowise.userservice.repository.interfaces.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString(exclude = "cards")
@NoArgsConstructor
public class User implements BaseEntity<Long> {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "surname")
    private String surname;

    @Setter
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Setter
    @Column(name = "email", unique = true)
    private String email;

    @Setter
    @Column(name = "active")
    private boolean active;

    @Setter
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<PaymentCard> cards = new ArrayList<>();

    public List<PaymentCard> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public void addCard(PaymentCard card) {
        cards.add(card);
        card.setUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}