package com.innowise.userservice.model;

import com.innowise.userservice.utils.Constants;
import com.innowise.userservice.utils.TestConfiguration;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.interfaces.PaymentCardRepository;
import com.innowise.userservice.model.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static com.innowise.userservice.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfiguration.class)
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository cardRepository;
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

        arya.setName(Constants.NAME_ARYA);
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
    void findUserByNameAndSurnameTest() {
        List<User> searchResult = userRepository.findUserByNameAndSurname(NAME_ARYA, STARK);
        assertThat(searchResult).isNotEmpty();
        assertEquals(STARK, searchResult.getFirst().getSurname());
    }

    @Test
    void findCardsByUserIdTest() {
        User arya = userRepository.findUserByNameAndSurname(NAME_ARYA, STARK).getFirst();
        testEntityManager.flush();
        List<PaymentCard> cards = cardRepository.findPaymentCardsByUserId(arya.getId());
        assertThat(cards).hasSize(2);
    }

    @Test
    void updateUserTest() {
        User arya = userRepository.findUserByNameAndSurname(NAME_ARYA, STARK).getFirst();
        testEntityManager.clear();
        User edited = new User(arya.getId(),
                NAME_NED,
                arya.getSurname(),
                arya.getBirthDate(),
                arya.getEmail(),
                arya.isActive(),
                arya.getCreatedAt(),
                arya.getUpdatedAt(),
                arya.getCards());

        User saved = userRepository.saveAndFlush(edited);
        testEntityManager.clear();
        assertNotEquals(arya.getName(), saved.getName());
        assertNotEquals(arya.getUpdatedAt(), saved.getUpdatedAt());
    }

    @Test
    void activateUserTest() {
        User ned = userRepository.findAll(UserSpecification.containsName(NAME_NED)).getFirst();
        long id = ned.getId();
        userRepository.activateUser(id);

        testEntityManager.flush();
        testEntityManager.clear();

        User newNed = userRepository.findById(id).orElseThrow();
        assertTrue(newNed.isActive());
    }
}