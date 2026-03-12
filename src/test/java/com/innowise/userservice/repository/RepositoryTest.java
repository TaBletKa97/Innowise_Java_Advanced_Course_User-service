package com.innowise.userservice.repository;

import com.innowise.userservice.repository.entity.PaymentCard;
import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.exceptions.ActivationException;
import com.innowise.userservice.repository.interfaces.UserRepository;
import com.innowise.userservice.service.UserSpecification;
import com.innowise.userservice.utils.TestsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.util.List;

import static com.innowise.userservice.utils.TestsConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaAuditing
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        User arya = new User();
        User ned = new User();
        User jon = new User();

        PaymentCard aryaCard1 = new PaymentCard();
        aryaCard1.setNumber(CARD_NUMBER1);
        aryaCard1.setHolder(HOLDER_NAME_ARYA);
        aryaCard1.setExpirationDate(LocalDate.now().plusYears(1));
        aryaCard1.setActive(true);

        PaymentCard aryaCard2 = new PaymentCard();
        aryaCard2.setNumber(CARD_NUMBER2);
        aryaCard2.setHolder(HOLDER_NAME_ARYA);
        aryaCard2.setExpirationDate(LocalDate.now().minusYears(1));
        aryaCard2.setActive(false);

        PaymentCard jonCard = new PaymentCard();
        jonCard.setNumber(CARD_NUMBER3);
        jonCard.setHolder(HOLDER_NAME_JON);
        jonCard.setExpirationDate(LocalDate.now().plusYears(2).minusMonths(3));
        jonCard.setActive(true);

        arya.setName(TestsConstants.NAME_ARYA);
        arya.setSurname(STARK);
        arya.setBirthDate(LocalDate.now().minusYears(25));
        arya.setEmail(EMAIL_ARYA);
        arya.setActive(false);
        arya.addCard(aryaCard1);
        arya.addCard(aryaCard2);

        ned.setName(NAME_NED);
        ned.setSurname(STARK);
        ned.setBirthDate(LocalDate.now().minusYears(45));
        ned.setEmail(EMAIL_NED);
        ned.setActive(false);

        jon.setName(NAME_JON);
        jon.setSurname(SNOW);
        jon.setBirthDate(LocalDate.now().minusYears(45));
        jon.setEmail(EMAIL_JON);
        jon.setActive(true);
        jon.addCard(jonCard);

        userRepository.save(arya);
        userRepository.save(ned);
        userRepository.save(jon);
    }

    @Test
    void OneToManyAndCascadeSavingAndAuditingTest() {
        List<User> all = userRepository.findAll();
        assertEquals(3, all.size());
        assertEquals(2, all.getFirst().getCards().size());
        assertNotNull(all.getFirst().getCreatedAt());
        assertNotNull(all.getFirst().getUpdatedAt());
    }

    @Test
    void findCardsByUserIdTest() {
        List<PaymentCard> cards = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(NAME_ARYA))
                .findFirst()
                .get()
                .getCards();

        assertThat(cards).hasSize(2);
    }

    @Test
    void updateUserTest() {
        User user = userRepository.findAll().getFirst();
        testEntityManager.flush();
        testEntityManager.clear();
        User edited = new User(user.getId(),
                NAME_NED,
                user.getSurname(),
                user.getBirthDate(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCards());

        User saved = userRepository.saveAndFlush(edited);
        testEntityManager.clear();
        assertNotEquals(user.getName(), saved.getName());
        assertNotEquals(user.getUpdatedAt(), saved.getUpdatedAt());
    }

    @Test
    void activateUserTest() throws ActivationException {
        User ned = userRepository.findAll(UserSpecification.containsName(NAME_NED)).getFirst();
        long id = ned.getId();
        userRepository.activateUser(id);

        assertThrows(ActivationException.class, () -> userRepository.activateUser(id));

        testEntityManager.flush();
        testEntityManager.clear();

        User newNed = userRepository.findById(id).orElseThrow();
        assertTrue(newNed.isActive());
    }
}