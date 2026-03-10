package com.innowise.userservice.controller;

import com.innowise.userservice.controller.DTO.CardRequestDTO;
import com.innowise.userservice.controller.DTO.CardResponseDTO;
import com.innowise.userservice.controller.exceptions.CardLimitViolationException;
import com.innowise.userservice.model.exceptions.ActivationException;
import com.innowise.userservice.model.exceptions.DeactivationException;
import com.innowise.userservice.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.innowise.userservice.utils.TestsConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CardServiceImplTest extends BaseTest {

    @Autowired
    private CardServiceImpl cardService;

    @Test
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void readAll() {
        List<CardResponseDTO> allCards = cardService.readAll();
        assertEquals(10, allCards.size());
    }

    @Test
    void readById() {
        CardResponseDTO card = cardService.readById(1L);
        assertEquals(1L, card.id());
    }

    @Test
    void create() {
        CardRequestDTO createRequest1 = new CardRequestDTO(null, 4L,
                CARD_NUMBER1, HOLDER_NAME_ARYA, LocalDate.now(), true);
        CardRequestDTO createRequest2 = new CardRequestDTO(null, 4L,
                CARD_NUMBER2, HOLDER_NAME_ARYA, LocalDate.now(), true);
        CardRequestDTO createRequest3 = new CardRequestDTO(null, 4L,
                CARD_NUMBER3, HOLDER_NAME_ARYA, LocalDate.now(), true);

        CardResponseDTO cardResponseDTO = cardService.create(createRequest1);
        assertNotNull(cardResponseDTO.createdAt());
        assertNotNull(cardResponseDTO.updatedAt());

        cardService.create(createRequest2);

        assertThrows(CardLimitViolationException.class,  () -> cardService.create(createRequest3));

        CardRequestDTO notValidRequest = new CardRequestDTO(null, 4L,
                CARD_NUMBER3 + "a", HOLDER_NAME_ARYA, LocalDate.now(), true);
        assertThrows(Throwable.class, () -> cardService.create(notValidRequest));
    }

    @Test
    void update() {
        CardResponseDTO oldData = cardService.readById(1L);
        CardResponseDTO update = cardService.update(new CardRequestDTO(1L, 1L,
                CARD_NUMBER1, null, LocalDate.now().plusYears(1), false));

        assertEquals(1L, update.id());
        assertEquals(CARD_NUMBER1, update.number());
        assertEquals(oldData.holder(), update.holder());
        assertEquals(oldData.createdAt(), update.createdAt());
        assertFalse(update.active());
        assertTrue(update.updatedAt().isAfter(oldData.updatedAt()));
        assertNotEquals(oldData.expirationDate(), update.expirationDate());

        assertThrows(UnsupportedOperationException.class, () -> cardService.update(new CardRequestDTO(1L, 2L,
                null, null, null, false)));
    }

    @Test
    void deleteById() {
        cardService.deleteById(1L);
        assertThrows(NoSuchElementException.class, () -> cardService.deleteById(1L));
    }

    @Test
    void readAllCardsByUserId() {
        List<CardResponseDTO> cards = cardService.readAllCardsByUserId(4L);
        assertEquals(3, cards.size());
    }

    @Test
    void activateCard() {
        cardService.activateCard(5L);
        CardResponseDTO activatedCard = cardService.readById(5L);
        assertTrue(activatedCard.active());

        assertThrows(ActivationException.class, () -> cardService.activateCard(5L));
    }

    @Test
    void deactivateCard() {
        cardService.deactivateCard(1L);
        CardResponseDTO activatedCard = cardService.readById(1L);
        assertFalse(activatedCard.active());

        assertThrows(DeactivationException.class, () -> cardService.deactivateCard(1L));
    }
}