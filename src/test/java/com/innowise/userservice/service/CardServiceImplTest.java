package com.innowise.userservice.service;

import com.innowise.userservice.exceptions.ActivationException;
import com.innowise.userservice.exceptions.DeactivationException;
import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.exceptions.CardLimitViolationException;
import com.innowise.userservice.service.implementations.CardServiceImpl;
import com.innowise.userservice.utils.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

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
        List<CardResponseDto> allCards = cardService.readAll();
        assertEquals(10, allCards.size());
    }

    @Test
    void readById() {
        CardResponseDto card = cardService.readById(1L);
        assertEquals(1L, card.id());
    }

    @Test
    void create() {
        CardRequestDto createRequest1 = new CardRequestDto(null, 4L,
                CARD_NUMBER1, HOLDER_NAME_ARYA, LocalDate.now(), true);
        CardRequestDto createRequest2 = new CardRequestDto(null, 4L,
                CARD_NUMBER2, HOLDER_NAME_ARYA, LocalDate.now(), true);
        CardRequestDto createRequest3 = new CardRequestDto(null, 4L,
                CARD_NUMBER3, HOLDER_NAME_ARYA, LocalDate.now(), true);

        CardResponseDto cardResponseDTO = cardService.create(createRequest1);
        assertNotNull(cardResponseDTO.createdAt());
        assertNotNull(cardResponseDTO.updatedAt());

        cardService.create(createRequest2);
        cardService.create(createRequest2);

        assertThrows(CardLimitViolationException.class,  () -> cardService.create(createRequest3));

        CardRequestDto notValidRequest = new CardRequestDto(null, 4L,
                CARD_NUMBER3 + "a", HOLDER_NAME_ARYA, LocalDate.now(), true);
        assertThrows(Throwable.class, () -> cardService.create(notValidRequest));
    }

    @Test
    void update() {
        CardResponseDto oldData = cardService.readById(1L);
        CardResponseDto update = cardService.update(1L, new CardRequestDto(1L, 1L,
                CARD_NUMBER1, null, LocalDate.now().plusYears(1), false));

        assertEquals(1L, update.id());
        assertEquals(CARD_NUMBER1, update.number());
        assertEquals(oldData.holder(), update.holder());
        assertEquals(oldData.createdAt(), update.createdAt());
        assertFalse(update.active());
        assertTrue(update.updatedAt().isAfter(oldData.updatedAt()));
        assertNotEquals(oldData.expirationDate(), update.expirationDate());

        CardRequestDto cardRequest = new CardRequestDto(1L, 2L, null, null, null, false);
        assertThrows(UnsupportedOperationException.class, () -> cardService.update(1L, cardRequest));
    }

    @Test
    void deleteById() {
        cardService.deleteById(1L);
        assertThrows(DeactivationException.class, () -> cardService.deleteById(1L));
    }

    @Test
    void readAllCardsByUserId() {
        List<CardResponseDto> cards = cardService.readAllCardsByUserId(4L);
        assertEquals(3, cards.size());
    }

    @Test
    void activateCard() {
        cardService.activateCard(5L);
        CardResponseDto activatedCard = cardService.readById(5L);
        assertTrue(activatedCard.active());

        assertThrows(ActivationException.class, () -> cardService.activateCard(5L));
    }

    @Test
    void deactivateCard() {
        cardService.deactivateCard(1L);
        CardResponseDto activatedCard = cardService.readById(1L);
        assertFalse(activatedCard.active());

        assertThrows(DeactivationException.class, () -> cardService.deactivateCard(1L));
    }
}